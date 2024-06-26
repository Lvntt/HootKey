package dev.banger.hootkey.presentation.state.vault_details

import androidx.compose.runtime.Immutable
import dev.banger.hootkey.presentation.entity.UiField

@Immutable
sealed interface VaultDetailsState {
    data object Loading : VaultDetailsState

    data class Content(
        val name: String,
        val categoryName: String,
        val fields: List<UiField>,
        val isFavorite: Boolean,
        val canFavorite: Boolean = true
    ) : VaultDetailsState

    data object Error : VaultDetailsState
}