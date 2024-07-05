package dev.banger.hootkey.domain.entity.password

sealed interface PasswordHealthScore {

    data object Calculating : PasswordHealthScore

    /**
     * score value is between 0 and 1
     */
    data class Score(
        val value: Float,
        val totalPasswordCount: Int,
        val strongPasswordCount: Int,
        val mediumPasswordCount: Int,
        val weakPasswordCount: Int
    ) : PasswordHealthScore

    data class Unknown(val throwable: Throwable? = null) : PasswordHealthScore

}