package dev.banger.hootkey.data.crypto

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
@OptIn(ExperimentalEncodingApi::class)
class SharedPrefsManager @Inject constructor(context: Context) {

    private companion object {
        const val HOOTKEY_PREFS = "hootkey_prefs"
        const val PASSWORD_HASH_ALIAS = "password_hash"
        const val SALT_ALIAS = "salt"
    }

    private val prefsMasterKeyAlias =
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private val sharedPrefs = EncryptedSharedPreferences.create(
        context,
        HOOTKEY_PREFS,
        prefsMasterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePasswordHash(passwordHash: String) {
        sharedPrefs.edit().putString(PASSWORD_HASH_ALIAS, passwordHash).apply()
    }

    fun getPasswordHash(): String? {
        return sharedPrefs.getString(PASSWORD_HASH_ALIAS, null)
    }

    fun saveSaltBase64(salt: String) {
        sharedPrefs.edit().putString(SALT_ALIAS, salt).apply()
    }

    fun getSalt(): ByteArray? {
        return sharedPrefs.getString(SALT_ALIAS, null)?.let { Base64.decode(it) }
    }

}