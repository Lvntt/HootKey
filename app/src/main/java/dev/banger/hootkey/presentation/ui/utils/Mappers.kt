package dev.banger.hootkey.presentation.ui.utils

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.TemplateField
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.entity.UiPasswordOptions
import dev.banger.hootkey.presentation.entity.UiPasswordStrength
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.state.new_template.NewTemplateState

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

fun UiTemplateField.toDomain(index: Int) = with(this) {
    TemplateField(
        index = index,
        name = name,
        type = type.toDomain()
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