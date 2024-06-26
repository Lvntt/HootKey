package dev.banger.hootkey.data.repository

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.password.PasswordSymbols
import dev.banger.hootkey.domain.entity.password.exception.EmptyCharPoolException
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PasswordRepositoryImpl(private val vaultRepository: VaultRepository) : PasswordRepository {

    private val _passwordHealthScore =
        MutableStateFlow<PasswordHealthScore>(PasswordHealthScore.Calculating)
    override val passwordHealthScore: StateFlow<PasswordHealthScore> =
        _passwordHealthScore.asStateFlow()

    override suspend fun calculatePasswordHealthScore() {
        runCatching {
            _passwordHealthScore.update {
                PasswordHealthScore.Calculating
            }
            var nextPageKey: String? = null
            var endReached = false

            var totalPasswordCount = 0
            var strongPasswordCount = 0
            var mediumPasswordCount = 0
            var weakPasswordCount = 0

            while (!endReached) {
                val vaults = vaultRepository.getAll(filter = FilterType.ALL, null, nextPageKey)
                vaults.vaults.forEach { vault ->
                    if (vault.password.isNullOrBlank()) return@forEach
                    totalPasswordCount++
                    val strength = checkPasswordStrength(vault.password)
                    if (strength == PasswordStrength.STRONG || strength == PasswordStrength.VERY_STRONG)
                        strongPasswordCount++
                    if (strength == PasswordStrength.MEDIUM)
                        mediumPasswordCount++
                    if (strength == PasswordStrength.WEAK || strength == PasswordStrength.VERY_WEAK)
                        weakPasswordCount++
                }

                nextPageKey = vaults.nextPageKey
                endReached = vaults.endReached
            }

            if (totalPasswordCount > 0) {
                _passwordHealthScore.update {
                    PasswordHealthScore.Score(
                        value =
                        (strongPasswordCount + mediumPasswordCount / 2f) / totalPasswordCount,
                        totalPasswordCount = totalPasswordCount,
                        strongPasswordCount = strongPasswordCount,
                        mediumPasswordCount = mediumPasswordCount,
                        weakPasswordCount = weakPasswordCount
                    )
                }
            } else {
                _passwordHealthScore.update {
                    PasswordHealthScore.Unknown
                }
            }
        }.onFailure {
            if (it is CancellationException) throw it
            _passwordHealthScore.update {
                PasswordHealthScore.Unknown
            }
        }
    }

    override fun generatePassword(options: PasswordOptions): GeneratedPassword {
        var charPool = charArrayOf()
        if (options.hasNumbers) charPool += PasswordSymbols.numbers
        if (options.hasSymbols) charPool += PasswordSymbols.special
        if (options.hasUppercase) charPool += PasswordSymbols.uppercase
        if (options.hasLowercase) charPool += PasswordSymbols.lowercase

        if (charPool.isEmpty()) throw EmptyCharPoolException()

        val password = (1..options.length)
            .map { charPool.random() }
            .joinToString("")

        val strength = checkPasswordStrength(password)

        return GeneratedPassword(password, strength)
    }

    override fun checkPasswordStrength(password: String): PasswordStrength {
        val length = password.length
        val hasNumbers = password.any { it.isDigit() }
        val hasSymbols = password.any { it in PasswordSymbols.special }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }

        val criteriaMet = listOf(hasNumbers, hasSymbols, hasUppercase, hasLowercase).count { it }

        return when {
            length >= 12 && criteriaMet >= 3 -> PasswordStrength.VERY_STRONG
            length >= 10 && criteriaMet >= 3 -> PasswordStrength.STRONG
            length >= 8 && criteriaMet >= 2 -> PasswordStrength.MEDIUM
            length >= 6 && criteriaMet >= 2 -> PasswordStrength.WEAK
            else -> PasswordStrength.VERY_WEAK
        }
    }

}