package dev.banger.hootkey.domain.entity.vault

data class VaultShort(
    val id: String,
    val name: String,
    val login: String?,
    val link: String?,
    val isFavourite: Boolean,
)
