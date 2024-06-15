package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.Vault
import dev.banger.hootkey.domain.entity.vault.VaultShort

interface VaultRepository {

    suspend fun getAll(filter: FilterType, query: String?): List<VaultShort>

    suspend fun getAllByCategory(categoryId: String, filter: FilterType, query: String?): List<VaultShort>

    suspend fun getById(id: String): Vault

    suspend fun create(vault: CreateVaultRequest): Vault

    suspend fun edit(vault: EditVaultRequest): Vault

    suspend fun delete(id: String)

    suspend fun notifyViewed(id: String)

    suspend fun addToFavourites(id: String)

    suspend fun removeFromFavourites(id: String)

}