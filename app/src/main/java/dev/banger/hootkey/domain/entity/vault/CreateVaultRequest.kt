package dev.banger.hootkey.domain.entity.vault

typealias Index = Int
typealias Value = String

data class CreateVaultRequest(
    val categoryId: String,
    val name: String,
    val fieldValues: Map<Index, Value>,
)
