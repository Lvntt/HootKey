package dev.banger.hootkey.data.crypto

import dev.banger.hootkey.domain.entity.auth.exception.UnauthorizedException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class PasswordValidator(private val sharedPrefsManager: SharedPrefsManager) {

    private companion object {
        const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA512"
        const val PBKDF2_ITERATION_COUNT: Int = 120000
        const val DERIVED_KEY_LENGTH = 256
    }

    private fun createHash(password: String): String {
        val secretKeyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
        val salt = sharedPrefsManager.getSalt() ?: throw UnauthorizedException("Salt is null")
        val keySpec =
            PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, DERIVED_KEY_LENGTH)
        return Base64.encode(secretKeyFactory.generateSecret(keySpec).encoded)
    }

    fun savePassword(password: String) {
        val passwordHash = createHash(password)
        sharedPrefsManager.savePasswordHash(passwordHash)
    }

    fun validatePassword(password: String): Boolean {
        val savedPasswordHash = sharedPrefsManager.getPasswordHash()
        return savedPasswordHash == createHash(password)
    }

}