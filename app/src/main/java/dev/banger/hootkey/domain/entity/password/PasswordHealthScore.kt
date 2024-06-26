package dev.banger.hootkey.domain.entity.password

sealed interface PasswordHealthScore {

    data object Calculating : PasswordHealthScore

    /**
     * score value is between 0 and 1
     */
    data class Score(val value: Float) : PasswordHealthScore

    data object Unknown : PasswordHealthScore

}