package dev.banger.hootkey.domain.entity.vault

data class VaultShort(
    val id: String,
    val name: String,
    val login: String?,
    val link: String?,
    val password: String?,
    val categoryId: String,
    val isFavourite: Boolean,
)
