package dev.banger.hootkey.data.crypto

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class CryptoManager {

    private companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val KEY_LENGTH = 256
        const val KEY_ALIAS = "hootkey_master"
        const val KEYSTORE_TYPE = "AndroidKeyStore"

        const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA512"
        const val PBKDF2_ITERATION_COUNT: Int = 300000
    }

    private val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun createKey(password: String): SecretKey {
        val secretKeyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
        //TODO replace salt with value from firebase db
        val salt = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val keySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, KEY_LENGTH)
        val secret = secretKeyFactory.generateSecret(keySpec).encoded
        return SecretKeySpec(secret, ALGORITHM)
    }

    private fun saveKey(key: SecretKey) {
        keyStore.setEntry(
            KEY_ALIAS,
            KeyStore.SecretKeyEntry(key),
            KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE).setEncryptionPaddings(PADDING).build()
        )
    }

    private fun getKey(): SecretKey? {
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }

    fun setMasterPassword(password: String) {
        val key = createKey(password)
        saveKey(key)
    }

    fun encrypt(data: ByteArray): ByteArray {
        val encryptedData = encryptCipher.doFinal(data)
        return ByteArrayOutputStream().apply {
            write(encryptCipher.iv.size)
            write(encryptCipher.iv)
            write(encryptedData.size)
            write(encryptedData)
        }.toByteArray()
    }

    fun decrypt(data: ByteArray): ByteArray {
        ByteArrayInputStream(data).use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedDataSize = it.read()
            val encryptedData = ByteArray(encryptedDataSize)
            it.read(encryptedData)

            return getDecryptCipherForIv(iv).doFinal(encryptedData)
        }
    }

    fun encryptBase64(data: String): String {
        return Base64.encode(encrypt(data.toByteArray(Charsets.UTF_8)))
    }

    fun decryptBase64(data: String): String {
        return decrypt(Base64.decode(data)).toString(Charsets.UTF_8)
    }

}