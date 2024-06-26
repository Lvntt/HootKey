package dev.banger.hootkey.presentation.ui.utils

import dev.banger.hootkey.data.Constants.EMPTY_STRING
import dev.banger.hootkey.domain.entity.category.Category
import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.Template
import dev.banger.hootkey.domain.entity.template.TemplateField
import dev.banger.hootkey.domain.entity.template.TemplateShort
import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.entity.vault.EditVaultRequest
import dev.banger.hootkey.domain.entity.vault.FieldValue
import dev.banger.hootkey.domain.entity.vault.Index
import dev.banger.hootkey.domain.entity.vault.Value
import dev.banger.hootkey.presentation.entity.UiCategory
import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiCategoryShort
import dev.banger.hootkey.presentation.entity.UiEditableTemplate
import dev.banger.hootkey.presentation.entity.UiEditableTemplateFieldShort
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.entity.UiPasswordOptions
import dev.banger.hootkey.presentation.entity.UiPasswordStrength
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.entity.UiTemplateShort
import dev.banger.hootkey.presentation.state.edit_vault.EditVaultState
import dev.banger.hootkey.presentation.state.new_category.NewCategoryState
import dev.banger.hootkey.presentation.state.new_template.NewTemplateState
import dev.banger.hootkey.presentation.state.new_vault.NewVaultState

fun UiPasswordOptions.toDomain() = with(this) {
    PasswordOptions(
        length = length,
        hasNumbers = hasNumbers,
        hasSymbols = hasSymbols,
        hasUppercase = hasUppercase,
        hasLowercase = hasLowercase
    )
}

fun PasswordStrength.toUi() = when (this) {
    PasswordStrength.COMPROMISED -> UiPasswordStrength.COMPROMISED
    PasswordStrength.VERY_WEAK -> UiPasswordStrength.VERY_WEAK
    PasswordStrength.WEAK -> UiPasswordStrength.WEAK
    PasswordStrength.MEDIUM -> UiPasswordStrength.MEDIUM
    PasswordStrength.STRONG -> UiPasswordStrength.STRONG
    PasswordStrength.VERY_STRONG -> UiPasswordStrength.VERY_STRONG
}

fun GeneratedPassword.toUi() = with(this) {
    UiGeneratedPassword(
        password = password,
        strength = strength.toUi()
    )
}

fun UiFieldType.toDomain() = when(this) {
    UiFieldType.TEXT -> FieldType.TEXT
    UiFieldType.LOGIN -> FieldType.LOGIN
    UiFieldType.PASSWORD -> FieldType.PASSWORD
    UiFieldType.SECRET -> FieldType.SECRET
    UiFieldType.LINK -> FieldType.LINK
    UiFieldType.DATE -> FieldType.DATE
}

fun FieldType.toUi() = when (this) {
    FieldType.TEXT -> UiFieldType.TEXT
    FieldType.LOGIN -> UiFieldType.LOGIN
    FieldType.PASSWORD -> UiFieldType.PASSWORD
    FieldType.SECRET -> UiFieldType.SECRET
    FieldType.LINK -> UiFieldType.LINK
    FieldType.DATE -> UiFieldType.DATE
}

fun UiTemplateField.toDomain(index: Int) = with(this) {
    TemplateField(
        index = index,
        name = name,
        type = type.toDomain()
    )
}

fun TemplateField.toUiEditableShort() = with(this) {
    UiEditableTemplateFieldShort(
        name = name,
        type = type.toUi()
    )
}

fun NewTemplateState.toCreateTemplateRequest() = with(this) {
    CreateTemplateRequest(
        name = name,
        fields = fields.mapIndexed { index, field ->
            field.toDomain(index)
        }
    )
}

fun NewCategoryState.toCreateCategoryRequest() = with(this) {
    CreateCategoryRequest(
        icon = icon.categoryIcon,
        name = name,
        templateId = template?.id ?: EMPTY_STRING
    )
}

fun TemplateShort.toUi() = with(this) {
    UiTemplateShort(
        id = id,
        name = name,
        isCustom = isCustom
    )
}

fun Template.toUi() = with(this) {
    val fieldValues = mutableMapOf<Index, Value>()
    fields.forEachIndexed { index, _ ->
        fieldValues[index] = EMPTY_STRING
    }

    UiEditableTemplate(
        id = id,
        name = name,
        isCustom = isCustom,
        fields = fields.map { it.toUiEditableShort() },
        fieldValues = fieldValues
    )
}

fun Template.toUi(fieldValues: Map<Index, FieldValue>) = with(this) {
    val editableFields = mutableListOf<UiEditableTemplateFieldShort>()

    val sortedKeys = fieldValues.keys.sorted()
    sortedKeys.forEach { key ->
        val field = fields[key]
        var value = fieldValues[key]?.value ?: EMPTY_STRING
        var valueMillis: Long? = null

        if (field.type == FieldType.DATE) {
            valueMillis = value.toLongOrNull()
            value = valueMillis?.let { formatDate(it) } ?: EMPTY_STRING
        }

        val uiEditableTemplateFieldShort = UiEditableTemplateFieldShort(
            name = field.name,
            type = field.type.toUi(),
            value = value,
            valueMillis = valueMillis
        )
        editableFields.add(uiEditableTemplateFieldShort)
    }

    UiEditableTemplate(
        id = id,
        name = name,
        isCustom = isCustom,
        fields = editableFields,
        // TODO useless field, to be removed
        fieldValues = mapOf()
    )
}

fun CategoryIcon.toUi() = when (this) {
    CategoryIcon.SOCIAL_MEDIA -> UiCategoryIcon.SOCIAL_MEDIA
    CategoryIcon.EMAIL -> UiCategoryIcon.EMAIL
    CategoryIcon.FINANCE -> UiCategoryIcon.FINANCE
    CategoryIcon.WORK -> UiCategoryIcon.WORK
    CategoryIcon.ENTERTAINMENT -> UiCategoryIcon.ENTERTAINMENT
    CategoryIcon.MISCELLANEOUS -> UiCategoryIcon.MISCELLANEOUS
}

fun Category.toUi() = with(this) {
    UiCategory(
        id = id,
        icon = icon.toUi(),
        name = name,
        template = template.toUi(),
        vaultsAmount = vaultsAmount,
        isCustom = isCustom
    )
}

fun Category.toUi(fieldValues: Map<Index, FieldValue>) = with(this) {
    UiCategory(
        id = id,
        icon = icon.toUi(),
        name = name,
        template = template.toUi(fieldValues),
        vaultsAmount = vaultsAmount,
        isCustom = isCustom
    )
}

fun CategoryShort.toUi() = with(this) {
    UiCategoryShort(
        id = id,
        icon = icon.toUi(),
        name = name,
        templateId = templateId,
        vaultsAmount = vaultsAmount,
        isCustom = isCustom
    )
}

fun NewVaultState.toCreateVaultRequest() = with(this) {
    val categoryId = category?.id ?: throw IllegalStateException("category cannot be null")
    val fieldValues = mutableMapOf<Index, Value>()
    category.template.fields.forEachIndexed { index, fieldValue ->
        when (fieldValue.type) {
            UiFieldType.DATE -> {
                val value = fieldValue.valueMillis?.toString() ?: EMPTY_STRING
                fieldValues[index] = value
            }
            else -> fieldValues[index] = fieldValue.value
        }
    }

    CreateVaultRequest(
        categoryId = categoryId,
        name = name,
        fieldValues = fieldValues
    )
}

fun EditVaultState.toEditVaultRequest() = with(this) {
    val categoryId = category?.id ?: throw IllegalStateException("category cannot be null")
    val fieldValues = mutableMapOf<Index, Value>()
    category.template.fields.forEachIndexed { index, fieldValue ->
        when (fieldValue.type) {
            UiFieldType.DATE -> {
                val value = fieldValue.valueMillis?.toString() ?: EMPTY_STRING
                fieldValues[index] = value
            }
            else -> fieldValues[index] = fieldValue.value
        }
    }

    EditVaultRequest(
        vaultId = vaultId,
        categoryId = categoryId,
        name = name,
        fieldValues = fieldValues
    )
}