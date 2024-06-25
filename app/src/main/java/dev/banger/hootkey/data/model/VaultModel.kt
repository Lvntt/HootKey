package dev.banger.hootkey.data.model

import com.google.firebase.Timestamp
import dev.banger.hootkey.data.Constants.EMPTY_STRING

data class VaultModel(
    val name: String = EMPTY_STRING,
    val categoryId: String = EMPTY_STRING,
    @field:JvmField
    val isFavourite: Boolean = false,
    val lastEditTime: Timestamp = Timestamp.now(),
    val lastViewedTime: Timestamp = Timestamp.now(),
    val login: String = EMPTY_STRING,
    val password: String = EMPTY_STRING,
    val link: String = EMPTY_STRING
)