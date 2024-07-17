package dev.banger.feature_auth_api.usecase

import dev.banger.feature_auth_api.api.AuthServiceApi

// TODO нужен ли?
class CheckUserLoggedInUseCase(private val authService: AuthServiceApi) {

    operator fun invoke() = authService.checkUserLoggedIn()

}