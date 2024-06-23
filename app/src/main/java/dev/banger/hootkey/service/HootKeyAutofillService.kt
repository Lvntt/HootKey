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
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get

class HootKeyAutofillService : AutofillService() {

    private companion object {
        const val TAG = "HootKeyAutofillService"
    }

    private val vaultRepository: VaultRepository = get()

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

        traverseStructure(
            structure,
            loginFieldsForAutofill,
            passwordFieldsForAutofill,
            appNameCandidates
        )

        if (loginFieldsForAutofill.isEmpty() && passwordFieldsForAutofill.isEmpty()) {
            callback.onFailure("no fields for autofill were found")
            return
        }

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

            val vaults = vaultRepository.getShortByIds(vaultIdsForAutofill.distinct())
            vaults.forEach { vault ->
                if (vault.login != null && vault.password != null) {
                    vaultsForAutofill.add(vault)
                }
            }
        }

        if (vaultsForAutofill.isEmpty()) {
            callback.onSuccess(null)
        }

        val datasetBuilder = Dataset.Builder()
        loginFieldsForAutofill.forEach { loginField ->
            vaultsForAutofill.forEach vaultsLoop@ { vault ->
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
            vaultsForAutofill.forEach vaultsLoop@ { vault ->
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
            .build()

        callback.onSuccess(fillResponse)
    }

    override fun onSaveRequest(p0: SaveRequest, p1: SaveCallback) {
        TODO("Not yet implemented")
    }

    private fun traverseStructure(
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
            traverseNode(
                viewNode,
                loginFieldsForAutofill,
                passwordFieldsForAutofill,
                appNameCandidates
            )
        }
    }

    private fun traverseNode(
        viewNode: ViewNode?,
        loginFieldsForAutofill: MutableList<ViewNode>,
        passwordFieldsForAutofill: MutableList<ViewNode>,
        appNameCandidates: MutableList<String>,
    ) {
        var isAutofillFieldFound = false
        val autofillHints = viewNode?.autofillHints

        if (autofillHints?.isNotEmpty() == true) {
            autofillHints.forEach { autofillHint ->
                val isLoginAutofillFieldCandidate = AutofillFieldCandidateHeuristics.loginHints.any {
                    autofillHint?.contains(it) == true
                }
                if (isLoginAutofillFieldCandidate) {
                    loginFieldsForAutofill.add(viewNode)
                    isAutofillFieldFound = true
                } else {
                    val isPasswordAutofillFieldCandidate = AutofillFieldCandidateHeuristics.passwordHints.any {
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
                val isPasswordAutofillFieldCandidate = AutofillFieldCandidateHeuristics.passwordHints.any {
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
            traverseNode(
                it,
                loginFieldsForAutofill,
                passwordFieldsForAutofill,
                appNameCandidates
            )
        }
    }

}