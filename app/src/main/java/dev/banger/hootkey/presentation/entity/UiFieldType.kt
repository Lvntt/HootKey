package dev.banger.hootkey.presentation.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.banger.hootkey.R

enum class UiFieldType(
    @DrawableRes val icon: Int?,
    @StringRes val text: Int,
    val keyboardType: KeyboardType,
    val visualTransformation: VisualTransformation
) {
    LOGIN(
        icon = R.drawable.ic_profile,
        text = R.string.login,
        keyboardType = KeyboardType.Email,
        visualTransformation = VisualTransformation.None
    ),
    PASSWORD(
        icon = R.drawable.ic_lock,
        text = R.string.password,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation()
    ),
    SECRET(
        icon = R.drawable.ic_lock,
        text = R.string.secret,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation()
    ),
    TEXT(
        icon = null,
        text = R.string.text,
        keyboardType = KeyboardType.Text,
        visualTransformation = VisualTransformation.None
    ),
    LINK(
        icon = R.drawable.ic_link,
        text = R.string.link,
        keyboardType = KeyboardType.Uri,
        visualTransformation = VisualTransformation.None
    ),
    DATE(
        icon = R.drawable.ic_calendar_month,
        text = R.string.date,
        keyboardType = KeyboardType.Number,
        visualTransformation = VisualTransformation.None
    ),
}