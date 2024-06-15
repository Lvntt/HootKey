package dev.banger.hootkey.domain.entity.vault

data class VaultsPage(
    val vaults: List<VaultShort>,
    val nextPageKey: String,
    val endReached: Boolean
)