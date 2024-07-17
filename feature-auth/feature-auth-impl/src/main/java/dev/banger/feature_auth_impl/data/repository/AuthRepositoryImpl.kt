package dev.banger.feature_auth_impl.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import dev.banger.core_common_api.CryptoManager
import dev.banger.feature_auth_api.exception.InvalidCredentialsException
import dev.banger.feature_auth_api.exception.RegistrationCollisionException
import dev.banger.feature_auth_api.model.UserInfoModel
import dev.banger.feature_auth_impl.domain.repository.AuthRepository
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import dev.banger.hootkey.data.crypto.SharedPrefsManager
import dev.banger.hootkey.data.datasource.SettingsManager
import dev.banger.hootkey.domain.repository.CategoryRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val cryptoManager: CryptoManager,
    private val passwordValidator: PasswordValidator,
    private val sharedPrefsManager: SharedPrefsManager,
    private val categoryRepository: CategoryRepository,
    private val settingsManager: SettingsManager
) : AuthRepository {

    private fun userInfo(userId: String) = fireStore.collection(userId).document("info")

    override suspend fun login(email: String, password: String) = runCatching {
        val userId = auth.signInWithEmailAndPassword(email, password).await().user?.uid
            ?: throw IllegalStateException("User id is null after login")

        val masterSalt = userInfo(userId).get().await().toObject<UserInfoModel>()?.salt
            ?: throw IllegalStateException("Salt is null after login")
        val validationSalt = cryptoManager.createSaltBase64()

        sharedPrefsManager.saveSaltBase64(validationSalt)
        setUpAutoSaveCategory()
        masterSalt
    }.fold(onSuccess = { masterSalt ->
        cryptoManager.setMasterPassword(password, masterSalt)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        auth.signOut()
        if (throwable is FirebaseAuthInvalidCredentialsException) throw InvalidCredentialsException()
        throw throwable
    })

    override suspend fun register(email: String, password: String) = runCatching {
        val userId = auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            ?: throw IllegalStateException("User id is null after registration")

        val masterSalt = cryptoManager.createSaltBase64()
        userInfo(userId).set(UserInfoModel(masterSalt)).await()
        val validationSalt = cryptoManager.createSaltBase64()
        sharedPrefsManager.saveSaltBase64(validationSalt)
        setUpAutoSaveCategory()
        masterSalt
    }.fold(onSuccess = { masterSalt ->
        cryptoManager.setMasterPassword(password, masterSalt)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        auth.currentUser?.delete()
        auth.signOut()
        if (throwable is FirebaseAuthUserCollisionException) throw RegistrationCollisionException()
        throw throwable
    })

    private suspend fun setUpAutoSaveCategory() {
        val autoSaveCategoryId =
            fireStore.collection("common")
                .document("autosave_category_id").get().await()
        if (autoSaveCategoryId.exists()) {
            autoSaveCategoryId.getString("id")?.let { id ->
                categoryRepository.getById(id)
                settingsManager.setAutoSaveCategoryId(id)
            }
        }
    }

    override fun checkUserLoggedIn() = auth.currentUser != null

    override suspend fun checkPassword(password: String): Boolean {
        return passwordValidator.validatePassword(password)
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }
}