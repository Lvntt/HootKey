package dev.banger.feature_auth_api.model

import com.example.core_utils.Constants.EMPTY_STRING

data class UserInfoModel(
    val salt: String = EMPTY_STRING
)