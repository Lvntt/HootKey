package dev.banger.hootkey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dev.banger.hootkey.data.crypto.CryptoManager
import dev.banger.hootkey.data.crypto.PasswordValidator
import dev.banger.hootkey.domain.entity.auth.exception.InvalidCredentialsException
import dev.banger.hootkey.domain.entity.auth.exception.RegistrationCollisionException
import dev.banger.hootkey.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val cryptoManager: CryptoManager,
    private val passwordValidator: PasswordValidator
) : AuthRepository {

    override suspend fun login(email: String, password: String) = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
    }.fold(onSuccess = {
        cryptoManager.setMasterPassword(password)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        if (throwable is FirebaseAuthInvalidCredentialsException) throw InvalidCredentialsException()
        throw throwable
    })

    override suspend fun register(email: String, password: String) = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
    }.fold(onSuccess = {
        cryptoManager.setMasterPassword(password)
        passwordValidator.savePassword(password)
    }, onFailure = { throwable ->
        if (throwable is FirebaseAuthUserCollisionException) throw RegistrationCollisionException()
        throw throwable
    })

    override suspend fun checkUserLoggedIn() = auth.currentUser != null

    override suspend fun checkPassword(password: String): Boolean {
        return passwordValidator.validatePassword(password)
    }
}