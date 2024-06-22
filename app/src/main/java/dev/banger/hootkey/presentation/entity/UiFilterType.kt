package dev.banger.hootkey.presentation.entity

import androidx.annotation.StringRes
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.vault.FilterType

enum class UiFilterType(@StringRes val labelResId: Int, val filterType: FilterType) {
    ALL(R.string.all, FilterType.ALL),
    FAVOuRITE(R.string.favourite, FilterType.FAVOURITE),
    RECENT(R.string.recent, FilterType.RECENT),
    LAST_EDIT(R.string.last_edit, FilterType.LAST_EDIT)
}