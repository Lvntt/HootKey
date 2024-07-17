package dev.banger.feature_auth_api.api

interface AuthServiceApi {

    fun checkUserLoggedIn(): Boolean

    suspend fun logout()
}