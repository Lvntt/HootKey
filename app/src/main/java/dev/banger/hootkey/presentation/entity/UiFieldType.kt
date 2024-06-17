package dev.banger.hootkey.presentation.entity

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.banger.hootkey.R

enum class UiFieldType(
    @DrawableRes val icon: Int?,
    val keyboardType: KeyboardType,
    val visualTransformation: VisualTransformation
) {
    TEXT(
        icon = null,
        keyboardType = KeyboardType.Text,
        visualTransformation = VisualTransformation.None
    ),
    LOGIN(
        icon = R.drawable.ic_profile,
        keyboardType = KeyboardType.Email,
        visualTransformation = VisualTransformation.None
    ),
    PASSWORD(
        icon = R.drawable.ic_lock,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation()
    ),
    SECRET(
        icon = R.drawable.ic_lock,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation()
    ),
    LINK(
        icon = R.drawable.ic_link,
        keyboardType = KeyboardType.Uri,
        visualTransformation = VisualTransformation.None
    ),
    DATE(
        icon = R.drawable.ic_calendar_month,
        keyboardType = KeyboardType.Number,
        visualTransformation = VisualTransformation.None
    ),
}