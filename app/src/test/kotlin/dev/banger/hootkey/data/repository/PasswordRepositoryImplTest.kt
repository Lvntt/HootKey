package dev.banger.hootkey.data.repository

import dev.banger.hootkey.Stubs
import dev.banger.hootkey.data.crypto.PasswordStrengthChecker
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.vault.VaultsPage
import dev.banger.hootkey.domain.repository.VaultRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

class PasswordRepositoryImplTest {

    @Test
    fun `calculatePasswordHealthScore with mixed strengths`() = runTest {
        val vaultRepository: VaultRepository = mock()
        val passwordStrengthChecker: PasswordStrengthChecker = mock()
        val passwordRepository = PasswordRepositoryImpl(vaultRepository, passwordStrengthChecker)
        val testVault = Stubs.testVaultShort
        val strongPassword = "strongPassword"
        val mediumPassword = "mediumPassword"
        val weakPassword = "weakPassword"
        val vaults = listOf(
            testVault.copy(password = strongPassword),
            testVault.copy(password = mediumPassword),
            testVault.copy(password = weakPassword)
        )
        val vaultsPage = VaultsPage(vaults, "", true)
        whenever(vaultRepository.getAll(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(vaultsPage)
        whenever(passwordStrengthChecker.checkPasswordStrength(strongPassword)).thenReturn(PasswordStrength.STRONG)
        whenever(passwordStrengthChecker.checkPasswordStrength(mediumPassword)).thenReturn(PasswordStrength.MEDIUM)
        whenever(passwordStrengthChecker.checkPasswordStrength(weakPassword)).thenReturn(PasswordStrength.WEAK)

        passwordRepository.calculatePasswordHealthScore()

        val healthScore = passwordRepository.passwordHealthScore.value
        assertTrue(healthScore is PasswordHealthScore.Score)
        val scoreValue = healthScore as PasswordHealthScore.Score
        assertEquals(0.5f, scoreValue.value)
        assertEquals(3, scoreValue.totalPasswordCount)
        assertEquals(1, scoreValue.strongPasswordCount)
        assertEquals(1, scoreValue.mediumPasswordCount)
        assertEquals(1, scoreValue.weakPasswordCount)
    }

}