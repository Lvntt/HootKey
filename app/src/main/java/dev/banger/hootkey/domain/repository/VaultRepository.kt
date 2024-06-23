package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.Vault
import dev.banger.hootkey.domain.entity.vault.VaultsPage

typealias VaultName = String
typealias VaultId = String

interface VaultRepository {

    suspend fun getAllNames(): Map<VaultName, VaultId>

    suspend fun getAll(filter: FilterType, query: String?, pageKey: String?): VaultsPage

    suspend fun getAllByCategory(
        categoryId: String,
        filter: FilterType,
        query: String?,
        pageKey: String?
    ): VaultsPage

    suspend fun getById(id: String): Vault

    suspend fun create(vault: CreateVaultRequest): Vault

    suspend fun edit(vault: EditVaultRequest): Vault

    suspend fun delete(id: String)

    suspend fun notifyViewed(id: String)

    suspend fun addToFavourites(id: String)

    suspend fun removeFromFavourites(id: String)

}