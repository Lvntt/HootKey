package dev.banger.hootkey.data.crypto

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class PasswordValidator(context: Context) {

    private companion object {
        const val PASSWORD_HASH_PREFS = "password_hash_prefs"
        const val PASSWORD_HASH_ALIAS = "password_hash"
        const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA512"
        const val PBKDF2_ITERATION_COUNT: Int = 300000
        const val DERIVED_KEY_LENGTH = 512
    }

    private val prefsMasterKeyAlias =
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private val sharedPrefs = EncryptedSharedPreferences.create(
        context,
        PASSWORD_HASH_PREFS,
        prefsMasterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun createHash(password: String): String {
        val secretKeyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
        //TODO replace salt with value from firebase db
        val salt = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val keySpec =
            PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, DERIVED_KEY_LENGTH)
        return Base64.encode(secretKeyFactory.generateSecret(keySpec).encoded)
    }

    fun savePassword(password: String) {
        val passwordHash = createHash(password)
        sharedPrefs.edit().putString(PASSWORD_HASH_ALIAS, passwordHash).apply()
    }

    fun validatePassword(password: String): Boolean {
        val savedPasswordHash = sharedPrefs.getString(PASSWORD_HASH_ALIAS, null)
        return savedPasswordHash == createHash(password)
    }

}