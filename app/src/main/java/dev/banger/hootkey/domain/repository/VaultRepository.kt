package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.Vault
import dev.banger.hootkey.domain.entity.vault.VaultShort

interface VaultRepository {

    fun getAll(filter: FilterType, query: String?): List<VaultShort>

    fun getAllByCategory(categoryId: String, filter: FilterType, query: String?): List<VaultShort>

    fun getById(id: String): Vault

    fun create(vault: CreateVaultRequest): Vault

    fun edit(vault: EditVaultRequest): Vault

    fun delete(id: String)

    fun notifyViewed(id: String)

    fun addToFavourites(id: String)

    fun removeFromFavourites(id: String)

}