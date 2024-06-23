package dev.banger.hootkey.service

import android.text.InputType

object AutofillFieldCandidateHeuristics {

    val loginHints = listOf("login", "email", "e-mail", "phone",
        "mobile", "account", "number", "логин", "почт",
        "телефон", "номер", "аккаунт")

    val passwordHints = listOf("pass", "pwd", "passwd", "code",
        "пароль", "код")

    val loginInputTypes = listOf(
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
        InputType.TYPE_CLASS_PHONE,
    )

    val passwordInputTypes = listOf(
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_NUMBER_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
    )
}