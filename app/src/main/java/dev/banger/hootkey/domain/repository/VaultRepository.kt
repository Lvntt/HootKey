package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.Vault
import dev.banger.hootkey.domain.entity.vault.VaultNameWithId
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.entity.vault.VaultsPage

interface VaultRepository {

    suspend fun getAllNames(): List<VaultNameWithId>

    suspend fun getShortByIds(ids: List<String>): List<VaultShort>

    /**
     * Intended for caching only (it does NOT return anything), most likely will take a while to load
     */
    suspend fun getAllFull()

    suspend fun getAll(filter: FilterType, query: String?, pageKey: String?): VaultsPage

    suspend fun getAllByCategory(
        categoryId: String,
        filter: FilterType,
        query: String?,
        pageKey: String?
    ): VaultsPage

    suspend fun getShortById(id: String): VaultShort

    suspend fun getById(id: String): Vault

    suspend fun create(vault: CreateVaultRequest): Vault

    suspend fun edit(vault: EditVaultRequest): Vault

    suspend fun delete(id: String)

    suspend fun notifyViewed(id: String)

    suspend fun addToFavourites(id: String)

    suspend fun removeFromFavourites(id: String)

}