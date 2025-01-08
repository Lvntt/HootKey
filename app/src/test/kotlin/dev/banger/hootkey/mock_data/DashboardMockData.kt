package dev.banger.hootkey.mock_data

import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.entity.vault.VaultsPage
import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiCategoryShort

object DashboardMockData {

    fun getVaultShort(
        id: String = "",
        name: String = "",
        login: String? = null,
        link: String? = null,
        password: String? = null,
        categoryId: String = "",
        isFavourite: Boolean = false,
    ): VaultShort = VaultShort(
        id = id,
        name = name,
        login = login,
        link = link,
        password = password,
        categoryId = categoryId,
        isFavourite = isFavourite,
    )

    fun getVaultsPage(
        vaults: List<VaultShort> = emptyList(),
        nextPageKey: String = "",
        endReached: Boolean = false,
    ): VaultsPage = VaultsPage(
        vaults = vaults,
        nextPageKey = nextPageKey,
        endReached = endReached,
    )

    fun getUiCategoryShort(
        id: String = "",
        icon: UiCategoryIcon = UiCategoryIcon.WORK,
        name: String = "",
        templateId: String = "",
        vaultsAmount: Int = 0,
        isCustom: Boolean = false,
    ): UiCategoryShort = UiCategoryShort(
        id = id,
        icon = icon,
        name = name,
        templateId = templateId,
        vaultsAmount = vaultsAmount,
        isCustom = isCustom,
    )

    fun getCategoryShort(
        id: String = "",
        icon: CategoryIcon = CategoryIcon.WORK,
        name: String = "",
        templateId: String = "",
        vaultsAmount: Int = 0,
        isCustom: Boolean = false,
    ): CategoryShort = CategoryShort(
        id = id,
        icon = icon,
        name = name,
        templateId = templateId,
        vaultsAmount = vaultsAmount,
        isCustom = isCustom,
    )
}