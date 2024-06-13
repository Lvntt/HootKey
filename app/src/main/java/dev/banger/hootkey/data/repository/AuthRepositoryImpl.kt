package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import dev.banger.hootkey.data.crypto.SharedPrefsManager
import dev.banger.hootkey.data.model.UserInfoModel
import dev.banger.hootkey.domain.entity.auth.exception.InvalidCredentialsException
import dev.banger.hootkey.domain.entity.auth.exception.RegistrationCollisionException
import dev.banger.hootkey.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val cryptoManager: CryptoManager,
    private val passwordValidator: PasswordValidator,
    private val sharedPrefsManager: SharedPrefsManager
) : AuthRepository {

    private fun userInfo(userId: String) = fireStore.collection(userId).document("info")

    override suspend fun login(email: String, password: String) = runCatching {
        val userId = auth.signInWithEmailAndPassword(email, password).await().user?.uid
            ?: throw IllegalStateException("User id is null after login")

        val salt = userInfo(userId).get().await().toObject<UserInfoModel>()?.salt
            ?: throw IllegalStateException("Salt is null after login")

        sharedPrefsManager.saveSaltBase64(salt)
    }.fold(onSuccess = {
        cryptoManager.setMasterPassword(password)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        auth.signOut()
        if (throwable is FirebaseAuthInvalidCredentialsException) throw InvalidCredentialsException()
        throw throwable
    })

    override suspend fun register(email: String, password: String) = runCatching {
        val userId = auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            ?: throw IllegalStateException("User id is null after registration")

        val salt = cryptoManager.createSaltBase64()
        userInfo(userId).set(UserInfoModel(salt)).await()
        sharedPrefsManager.saveSaltBase64(salt)
    }.fold(onSuccess = {
        cryptoManager.setMasterPassword(password)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        auth.currentUser?.delete()
        auth.signOut()
        if (throwable is FirebaseAuthUserCollisionException) throw RegistrationCollisionException()
        throw throwable
    })

    override fun checkUserLoggedIn() = auth.currentUser != null

    override suspend fun checkPassword(password: String): Boolean {
        return passwordValidator.validatePassword(password)
    }
}