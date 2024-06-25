package dev.banger.hootkey.service

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.Field
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get

class HootKeyAutofillService : AutofillService() {

    private companion object {
        const val TAG = "HootKeyAutofillService"
    }

    private val vaultRepository: VaultRepository = get()
    private val categoryRepository: CategoryRepository = get()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "error fetching vaults\n ${throwable.stackTraceToString()}")
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val loginFieldsForAutofill = mutableListOf<ViewNode>()
        val passwordFieldsForAutofill = mutableListOf<ViewNode>()
        val appNameCandidates = mutableListOf<String>()
        val vaultsForAutofill = mutableListOf<VaultShort>()

        val context = request.fillContexts
        val structure = context[context.lastIndex].structure

        traverseStructureForFill(
            structure,
            loginFieldsForAutofill,
            passwordFieldsForAutofill,
            appNameCandidates
        )

        if (loginFieldsForAutofill.isEmpty() && passwordFieldsForAutofill.isEmpty()) {
            callback.onFailure("no fields for autofill were found")
            return
        }

        val saveInfo = SaveInfo.Builder(
            SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
            arrayOf(
                loginFieldsForAutofill.first().autofillId,
                passwordFieldsForAutofill.first().autofillId
            )
        ).build()

        val vaultIdsForAutofill = mutableListOf<String>()

        runBlocking(exceptionHandler) {
            val vaultNames = vaultRepository.getAllNames()
            vaultNames.forEach { vault ->
                val vaultNameLowered = vault.name.lowercase()
                appNameCandidates.forEach { appNameCandidate ->
                    if (appNameCandidate.contains(vaultNameLowered)
                        || vaultNameLowered.contains(appNameCandidate)
                    ) {
                        vaultIdsForAutofill.add(vault.id)
                    }
                }
            }

            if (vaultIdsForAutofill.isEmpty()) return@runBlocking
            val vaults = vaultRepository.getShortByIds(vaultIdsForAutofill.distinct())
            vaults.forEach { vault ->
                if (vault.login != null && vault.password != null) {
                    vaultsForAutofill.add(vault)
                }
            }
        }

        val datasetBuilder = Dataset.Builder()

        if (vaultsForAutofill.isEmpty()) {
            val notUsed = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            val loginId = loginFieldsForAutofill.first().autofillId
            val passwordId = passwordFieldsForAutofill.first().autofillId
            if (loginId != null && passwordId != null) {
                @Suppress("DEPRECATION")
                datasetBuilder.setValue(
                    loginId,
                    null,
                    notUsed
                )
                @Suppress("DEPRECATION")
                datasetBuilder.setValue(
                    passwordId,
                    null,
                    notUsed
                )
                val fillResponse = FillResponse.Builder()
                    .addDataset(datasetBuilder.build())
                    .setSaveInfo(saveInfo)
                    .build()
                callback.onSuccess(fillResponse)
                return
            }
            callback.onSuccess(null)
            return
        }

        loginFieldsForAutofill.forEach { loginField ->
            vaultsForAutofill.forEach vaultsLoop@{ vault ->
                val autofillId = loginField.autofillId ?: return@vaultsLoop

                val autofillValue = AutofillValue.forText(vault.login)
                val remoteViews = RemoteViews(packageName, R.layout.item_autofill_value)
                remoteViews.setTextViewText(R.id.autofillText, "${vault.name} • ${vault.login}")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val presentation = Presentations.Builder()
                        .setMenuPresentation(remoteViews)
                        .build()

                    val datasetField = Field.Builder()
                        .setValue(autofillValue)
                        .setPresentations(presentation)
                        .build()

                    datasetBuilder.setField(
                        autofillId,
                        datasetField
                    )
                } else {
                    @Suppress("DEPRECATION")
                    datasetBuilder.setValue(
                        autofillId,
                        autofillValue,
                        remoteViews
                    )
                }
            }
        }
        passwordFieldsForAutofill.forEach { passwordField ->
            vaultsForAutofill.forEach vaultsLoop@{ vault ->
                val autofillId = passwordField.autofillId ?: return@vaultsLoop

                val autofillValue = AutofillValue.forText(vault.password)
                val remoteViews = RemoteViews(packageName, R.layout.item_autofill_value)
                remoteViews.setTextViewText(R.id.autofillText, vault.name)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val presentation = Presentations.Builder()
                        .setMenuPresentation(remoteViews)
                        .build()

                    val datasetField = Field.Builder()
                        .setValue(autofillValue)
                        .setPresentations(presentation)
                        .build()

                    datasetBuilder.setField(
                        autofillId,
                        datasetField
                    )
                } else {
                    @Suppress("DEPRECATION")
                    datasetBuilder.setValue(
                        autofillId,
                        autofillValue,
                        remoteViews
                    )
                }
            }
        }

        val dataset = datasetBuilder.build()

        val fillResponse = FillResponse.Builder()
            .addDataset(dataset)
            .setSaveInfo(saveInfo)
            .build()

        callback.onSuccess(fillResponse)
    }

    private fun traverseStructureForFill(
        structure: AssistStructure,
        loginFieldsForAutofill: MutableList<ViewNode>,
        passwordFieldsForAutofill: MutableList<ViewNode>,
        appNameCandidates: MutableList<String>,
    ) {
        val appPackageName = structure.activityComponent.packageName
        val packageManager = applicationContext.packageManager
        try {
            val applicationInfo = packageManager.getApplicationInfo(appPackageName, 0)
            val applicationName = packageManager.getApplicationLabel(applicationInfo).toString()
            appNameCandidates.add(applicationName)
        } catch (e: Exception) {
            Log.e(TAG, "could not get application info")
        }
        appNameCandidates.add(appPackageName)

        val windowNodes = structure.run {
            (0 until windowNodeCount).map { getWindowNodeAt(it) }
        }
        windowNodes.forEach {
            val viewNode = it.rootViewNode
            traverseNodeForFill(
                viewNode,
                loginFieldsForAutofill,
                passwordFieldsForAutofill,
                appNameCandidates
            )
        }
    }

    private fun traverseNodeForFill(
        viewNode: ViewNode?,
        loginFieldsForAutofill: MutableList<ViewNode>,
        passwordFieldsForAutofill: MutableList<ViewNode>,
        appNameCandidates: MutableList<String>,
    ) {
        var isAutofillFieldFound = false
        val autofillHints = viewNode?.autofillHints

        if (autofillHints?.isNotEmpty() == true) {
            autofillHints.forEach { autofillHint ->
                val isLoginAutofillFieldCandidate =
                    AutofillFieldCandidateHeuristics.loginHints.any {
                        autofillHint?.contains(it) == true
                    }
                if (isLoginAutofillFieldCandidate) {
                    loginFieldsForAutofill.add(viewNode)
                    isAutofillFieldFound = true
                } else {
                    val isPasswordAutofillFieldCandidate =
                        AutofillFieldCandidateHeuristics.passwordHints.any {
                            autofillHint?.contains(it) == true
                        }
                    if (isPasswordAutofillFieldCandidate) {
                        passwordFieldsForAutofill.add(viewNode)
                        isAutofillFieldFound = true
                    }
                }
            }
        }

        if (viewNode != null && !isAutofillFieldFound) {
            val hint = viewNode.hint?.lowercase()
            val text = viewNode.text?.toString()?.lowercase()
            val viewId = viewNode.idEntry
            val inputType = viewNode.inputType

            if (!hint.isNullOrBlank()) {
                appNameCandidates.add(hint)
            }
            if (!text.isNullOrBlank()) {
                appNameCandidates.add(text)
            }
            if (!viewId.isNullOrBlank()) {
                appNameCandidates.add(viewId)
            }

            val isLoginAutofillFieldCandidate = AutofillFieldCandidateHeuristics.loginHints.any {
                hint?.contains(it) == true || text?.contains(it) == true
            } || AutofillFieldCandidateHeuristics.loginInputTypes.any {
                inputType == it
            }

            if (isLoginAutofillFieldCandidate) {
                loginFieldsForAutofill.add(viewNode)
            } else {
                val isPasswordAutofillFieldCandidate =
                    AutofillFieldCandidateHeuristics.passwordHints.any {
                        hint?.contains(it) == true || text?.contains(it) == true
                    } || AutofillFieldCandidateHeuristics.passwordInputTypes.any {
                        inputType == it
                    }

                if (isPasswordAutofillFieldCandidate) {
                    passwordFieldsForAutofill.add(viewNode)
                }
            }
        }

        val nodeChildren = viewNode?.run {
            (0 until childCount).map { getChildAt(it) }
        }
        nodeChildren?.forEach {
            traverseNodeForFill(
                it,
                loginFieldsForAutofill,
                passwordFieldsForAutofill,
                appNameCandidates
            )
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context = request.fillContexts
        val structure = context[context.lastIndex].structure

        val appPackageName = structure.activityComponent.packageName
        val packageManager = applicationContext.packageManager
        val appName: String

        try {
            val applicationInfo = packageManager.getApplicationInfo(appPackageName, 0)
            appName = packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            callback.onFailure("could not get application name")
            return
        }

        val loginsForAutosave = mutableListOf<ViewNode>()
        val passwordsForAutosave = mutableListOf<ViewNode>()

        traverseStructureForSave(
            structure,
            loginsForAutosave,
            passwordsForAutosave
        )

        if (loginsForAutosave.isNotEmpty() && passwordsForAutosave.isNotEmpty()) {
            val loginNode = loginsForAutosave.first()
            val passwordNode = passwordsForAutosave.first()

            if (
                loginNode.autofillId != null && loginNode.autofillValue?.textValue?.isNotBlank() == true
                && passwordNode.autofillId != null && passwordNode.autofillValue?.textValue?.isNotBlank() == true
            ) {
                val login = loginNode.autofillValue?.textValue
                val password = passwordNode.autofillValue?.textValue
                if (login == null || password == null) {
                    callback.onFailure("could not get login or password")
                    return
                }
                runBlocking {
                    val autosaveCategoryId = categoryRepository.getAutoSaveCategoryId()
                    if (autosaveCategoryId == null) {
                        callback.onFailure("could not load autosave category")
                    } else {
                        runCatching {
                            vaultRepository.create(
                                CreateVaultRequest(
                                    categoryId = autosaveCategoryId,
                                    name = appName,
                                    fieldValues = mapOf(
                                        0 to login.toString(),
                                        1 to password.toString()
                                    )
                                )
                            )
                        }.onFailure {
                            callback.onFailure("error saving")
                        }.onSuccess {
                            callback.onSuccess()
                        }
                    }
                }
            }
        }
    }

    private fun traverseStructureForSave(
        structure: AssistStructure,
        loginsForAutosave: MutableList<ViewNode>,
        passwordsForAutosave: MutableList<ViewNode>,
    ) {
        val windowNodes = structure.run {
            (0 until windowNodeCount).map { getWindowNodeAt(it) }
        }
        windowNodes.forEach {
            val viewNode = it.rootViewNode
            traverseNodeForSave(
                viewNode,
                loginsForAutosave,
                passwordsForAutosave
            )
        }
    }

    private fun traverseNodeForSave(
        viewNode: ViewNode?,
        loginsForAutosave: MutableList<ViewNode>,
        passwordsForAutosave: MutableList<ViewNode>,
    ) {
        val autofillHints = viewNode?.autofillHints
        var isAutofillFieldFound = false

        if (autofillHints?.isNotEmpty() == true) {
            autofillHints.forEach { autofillHint ->
                val isLoginAutosaveCandidate = AutofillFieldCandidateHeuristics.loginHints.any {
                    autofillHint?.contains(it) == true
                }
                val viewNodeText = viewNode.text?.toString()
                if (isLoginAutosaveCandidate && viewNodeText != null) {
                    loginsForAutosave.add(viewNode)
                    isAutofillFieldFound = true
                } else {
                    val isPasswordAutosaveCandidate =
                        AutofillFieldCandidateHeuristics.passwordHints.any {
                            autofillHint?.contains(it) == true
                        }
                    if (isPasswordAutosaveCandidate && viewNodeText != null) {
                        passwordsForAutosave.add(viewNode)
                        isAutofillFieldFound = true
                    }
                }
            }
        }

        if (viewNode != null && !isAutofillFieldFound) {
            val hint = viewNode.hint?.lowercase()
            val text = viewNode.text?.toString()?.lowercase()
            val inputType = viewNode.inputType

            val isLoginAutosaveCandidate = AutofillFieldCandidateHeuristics.loginHints.any {
                hint?.contains(it) == true || text?.contains(it) == true
            } || AutofillFieldCandidateHeuristics.loginInputTypes.any {
                inputType == it
            }

            if (isLoginAutosaveCandidate) {
                loginsForAutosave.add(viewNode)
            } else {
                val isPasswordAutosaveCandidate =
                    AutofillFieldCandidateHeuristics.passwordHints.any {
                        hint?.contains(it) == true || text?.contains(it) == true
                    } || AutofillFieldCandidateHeuristics.passwordInputTypes.any {
                        inputType == it
                    }

                if (isPasswordAutosaveCandidate) {
                    passwordsForAutosave.add(viewNode)
                }
            }
        }

        val nodeChildren = viewNode?.run {
            (0 until childCount).map { getChildAt(it) }
        }
        nodeChildren?.forEach {
            traverseNodeForSave(
                it,
                loginsForAutosave,
                passwordsForAutosave
            )
        }
    }

}