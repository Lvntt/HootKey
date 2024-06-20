package dev.banger.hootkey.presentation.entity

import androidx.annotation.DrawableRes
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.category.CategoryIcon

enum class UiCategoryIcon(
    val categoryIcon: CategoryIcon,
    @DrawableRes val icon: Int
) {
    SOCIAL_MEDIA(
        CategoryIcon.SOCIAL_MEDIA,
        R.drawable.ic_groups
    ),
    EMAIL(
        CategoryIcon.EMAIL,
        R.drawable.ic_mail
    ),
    FINANCE(
        CategoryIcon.FINANCE,
        R.drawable.ic_paid
    ),
    WORK(
        CategoryIcon.WORK,
        R.drawable.ic_business_center
    ),
    ENTERTAINMENT(
        CategoryIcon.ENTERTAINMENT,
        R.drawable.ic_esports
    ),
    MISCELLANEOUS(
        CategoryIcon.MISCELLANEOUS,
        R.drawable.ic_other
    ),
}