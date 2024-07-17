package dev.banger.core_common_api

interface CryptoManager {

    fun encrypt(data: ByteArray): ByteArray

    fun decrypt(data: ByteArray): ByteArray

    fun encryptBase64(data: String): String

    fun decryptBase64(data: String): String

    fun createSaltBase64(): String
}