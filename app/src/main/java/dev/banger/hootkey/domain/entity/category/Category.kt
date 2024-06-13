package dev.banger.hootkey.domain.entity.category

import dev.banger.hootkey.domain.entity.template.Template

data class Category(
    val id: String,
    val icon: CategoryIcon,
    val name: String,
    val template: Template,
    val vaultsAmount: Int,
    val isCustom: Boolean
)
