package dev.banger.hootkey.data.crypto

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.KeyStore
import java.security.SecureRandom
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
        const val PBKDF2_ITERATION_COUNT = 300000
        const val SALT_LENGTH = 16

        const val SECURE_RANDOM_ALGORITHM = "SHA1PRNG"
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

    private fun createSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM).nextBytes(salt)
        return salt
    }

    private fun createKey(password: String, salt: ByteArray): SecretKey {
        val secretKeyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
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

    fun setMasterPassword(password: String, salt: String) {
        val key = createKey(password, Base64.decode(salt))
        saveKey(key)
    }

    fun encrypt(data: ByteArray): ByteArray {
        val encryptCipherInstance = encryptCipher
        val encryptedData = encryptCipherInstance.doFinal(data)
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).apply {
            writeInt(encryptCipherInstance.iv.size)
            write(encryptCipherInstance.iv)

            writeInt(encryptedData.size)
            write(encryptedData)
        }
        return byteStream.toByteArray()
    }

    fun decrypt(data: ByteArray): ByteArray {
        DataInputStream(ByteArrayInputStream(data)).use {
            val ivSize = it.readInt()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedDataSize = it.readInt()
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

    fun createSaltBase64(): String {
        return Base64.encode(createSalt())
    }

}