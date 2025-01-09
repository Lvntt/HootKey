package dev.banger.hootkey.presentation.viewmodel

import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.mock_data.DashboardMockData
import dev.banger.hootkey.presentation.helpers.DashboardStateHelper
import dev.banger.hootkey.presentation.intent.DashboardIntent
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val vaultRepository: VaultRepository = mockk(relaxed = true)
    private val passwordRepository: PasswordRepository = mockk(relaxed = true)
    private val defaultDispatcher = UnconfinedTestDispatcher()
    private val stateHelper = DashboardStateHelper()

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = DashboardViewModel(
            categoryRepository = categoryRepository,
            vaultRepository = vaultRepository,
            passwordRepository = passwordRepository,
            defaultDispatcher = defaultDispatcher,
            stateHelper = stateHelper,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `trigger dismissVaultDetails updates vaultDetails correctly`() {
        // given
        val initialVaultDetails = DashboardMockData.getVaultShort(id = "123")
        val openDetailIntent = DashboardIntent.OpenVaultDetails(initialVaultDetails)
        val dismissDetailsIntent = DashboardIntent.DismissVaultDetails
        val expected = null

        // when
        viewModel.dispatch(openDetailIntent)
        viewModel.dispatch(dismissDetailsIntent)
        val actual = viewModel.state.value.vaultDetails

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger openVaultDetails updates vaultDetails correctly`() {
        // given
        val vaultDetails = DashboardMockData.getVaultShort(id = "123")
        val openDetailIntent = DashboardIntent.OpenVaultDetails(vaultDetails)
        val expected = vaultDetails

        // when
        viewModel.dispatch(openDetailIntent)
        val actual = viewModel.state.value.vaultDetails

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger changeCategoriesVaultsCount updates categories correctly`() {
        // given
        val initialCategories = listOf(
            DashboardMockData.getCategoryShort(id = "1", vaultsAmount = 2),
            DashboardMockData.getCategoryShort(id = "2", vaultsAmount = 3),
            DashboardMockData.getCategoryShort(id = "3", vaultsAmount = 4),
        )
        val deletedCategoryIds = listOf("1")
        val addedCategoryIds = listOf("3")

        val expectedCategories = listOf(
            DashboardMockData.getUiCategoryShort(id = "3", vaultsAmount = 5),
            DashboardMockData.getUiCategoryShort(id = "2", vaultsAmount = 3),
            DashboardMockData.getUiCategoryShort(id = "1", vaultsAmount = 1),
        )

        val loadCategoriesIntent = DashboardIntent.LoadCategories
        val changeCategoriesVaultsCountIntent = DashboardIntent.ChangeCategoriesVaultsCount(
            deletedCategoryIds = deletedCategoryIds,
            addedCategoryIds = addedCategoryIds
        )

        coEvery { categoryRepository.getAllShort() } returns initialCategories
        coEvery {
            categoryRepository.getShortById("3")
        } returns DashboardMockData.getCategoryShort(id = "3", vaultsAmount = 4)

        // when
        viewModel.dispatch(loadCategoriesIntent)
        viewModel.dispatch(changeCategoriesVaultsCountIntent)

        // then
        val actualCategories = viewModel.state.value.categories
        Assert.assertEquals(expectedCategories, actualCategories)
    }

    @Test
    fun `trigger removeDeletedVaults updates vaults correctly`() {
        // given
        val initialVaults = listOf(
            DashboardMockData.getVaultShort(id = "1"),
            DashboardMockData.getVaultShort(id = "2"),
        )
        val loadVaultsIntent = DashboardIntent.LoadNextVaultsPage
        val removeDeletedVaultsIntent = DashboardIntent.RemoveDeletedVaults(vaultIds = listOf("2"))
        val expected = listOf(DashboardMockData.getVaultShort(id = "1"))

        coEvery {
            vaultRepository.getAll(any(), any(), any())
        } returns DashboardMockData.getVaultsPage(vaults = initialVaults)

        // when
        viewModel.dispatch(loadVaultsIntent)
        viewModel.dispatch(removeDeletedVaultsIntent)
        val actual = viewModel.state.value.vaults

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger updateVaults updates vaults correctly`() {
        // given
        val vaultIds = listOf("2")
        val initialVaults = listOf(
            DashboardMockData.getVaultShort(id = "1"),
            DashboardMockData.getVaultShort(id = "2", name = "name1"),
        )
        val loadVaultsIntent = DashboardIntent.LoadNextVaultsPage
        val updateVaultsIntent = DashboardIntent.UpdateVaults(vaultIds = vaultIds)
        val expected = listOf(
            DashboardMockData.getVaultShort(id = "1"),
            DashboardMockData.getVaultShort(id = "2", name = "name2")
        )

        coEvery {
            vaultRepository.getAll(any(), any(), any())
        } returns DashboardMockData.getVaultsPage(vaults = initialVaults)
        coEvery {
            vaultRepository.getShortByIds(vaultIds)
        } returns listOf(DashboardMockData.getVaultShort(id = "2", name = "name2"))

        // when
        viewModel.dispatch(loadVaultsIntent)
        viewModel.dispatch(updateVaultsIntent)
        val actual = viewModel.state.value.vaults

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger addNewVault updates vaults correctly`() {
        // given
        val vaultId = "3"
        val initialVaults = listOf(
            DashboardMockData.getVaultShort(id = "1"),
            DashboardMockData.getVaultShort(id = "2"),
        )
        val loadVaultsIntent = DashboardIntent.LoadNextVaultsPage
        val addNewVaultIntent = DashboardIntent.AddNewVault(vaultId = vaultId)
        val expected = listOf(
            DashboardMockData.getVaultShort(id = "3"),
            DashboardMockData.getVaultShort(id = "1"),
            DashboardMockData.getVaultShort(id = "2"),
        )

        coEvery {
            vaultRepository.getAll(any(), any(), any())
        } returns DashboardMockData.getVaultsPage(vaults = initialVaults)
        coEvery {
            vaultRepository.getShortById(vaultId)
        } returns DashboardMockData.getVaultShort(id = vaultId)

        // when
        viewModel.dispatch(loadVaultsIntent)
        viewModel.dispatch(addNewVaultIntent)
        val actual = viewModel.state.value.vaults

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger updateVault updates vaults correctly`() {
        // given
        val vaultId = "1"
        val initialVaults = listOf(
            DashboardMockData.getVaultShort(id = "1", name = "name1"),
            DashboardMockData.getVaultShort(id = "2"),
        )
        val loadVaultsIntent = DashboardIntent.LoadNextVaultsPage
        val updateVaultIntent = DashboardIntent.UpdateVault(vaultId = vaultId)
        val expected = listOf(
            DashboardMockData.getVaultShort(id = "1", name = "name2"),
            DashboardMockData.getVaultShort(id = "2"),
        )

        coEvery {
            vaultRepository.getAll(any(), any(), any())
        } returns DashboardMockData.getVaultsPage(vaults = initialVaults)
        coEvery {
            vaultRepository.getShortById(vaultId)
        } returns DashboardMockData.getVaultShort(id = vaultId, name = "name2")

        // when
        viewModel.dispatch(loadVaultsIntent)
        viewModel.dispatch(updateVaultIntent)
        val actual = viewModel.state.value.vaults

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger decrementCategoryVaultsCount updates categories correctly`() {
        // given
        val categoryId = "1"
        val initialCategories = listOf(
            DashboardMockData.getCategoryShort(id = "1", vaultsAmount = 4),
            DashboardMockData.getCategoryShort(id = "2", vaultsAmount = 2),
        )
        val loadCategoriesIntent = DashboardIntent.LoadCategories
        val decrementCategoryVaultsCountIntent = DashboardIntent.DecrementCategoryVaultsCount(categoryId = categoryId)
        val expected = listOf(
            DashboardMockData.getUiCategoryShort(id = "1", vaultsAmount = 3),
            DashboardMockData.getUiCategoryShort(id = "2", vaultsAmount = 2),
        )

        coEvery { categoryRepository.getAllShort() } returns initialCategories

        // when
        viewModel.dispatch(loadCategoriesIntent)
        viewModel.dispatch(decrementCategoryVaultsCountIntent)
        val actual = viewModel.state.value.categories

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger incrementCategoryVaultsCount updates categories correctly`() {
        // given
        val categoryId = "1"
        val initialCategories = listOf(
            DashboardMockData.getCategoryShort(id = "1", vaultsAmount = 4),
            DashboardMockData.getCategoryShort(id = "2", vaultsAmount = 2),
        )
        val loadCategoriesIntent = DashboardIntent.LoadCategories
        val incrementCategoryVaultsCountIntent = DashboardIntent.IncrementCategoryVaultsCount(categoryId = categoryId)
        val expected = listOf(
            DashboardMockData.getUiCategoryShort(id = "1", vaultsAmount = 5),
            DashboardMockData.getUiCategoryShort(id = "2", vaultsAmount = 2),
        )

        coEvery { categoryRepository.getAllShort() } returns initialCategories

        // when
        viewModel.dispatch(loadCategoriesIntent)
        viewModel.dispatch(incrementCategoryVaultsCountIntent)
        val actual = viewModel.state.value.categories

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger deleteVault updates vaults and categories correctly`() {
        // given
        val vaultToDelete = DashboardMockData.getVaultShort(id = "1", name = "name1", categoryId = "1")
        val initialVaults = listOf(
            vaultToDelete,
            DashboardMockData.getVaultShort(id = "2"),
        )
        val initialCategories = listOf(
            DashboardMockData.getCategoryShort(id = "1", vaultsAmount = 4),
            DashboardMockData.getCategoryShort(id = "2", vaultsAmount = 2),
        )

        val loadCategoriesIntent = DashboardIntent.LoadCategories
        val loadVaultsIntent = DashboardIntent.LoadNextVaultsPage
        val openDeleteDialogIntent = DashboardIntent.OpenDeleteDialog(vault = vaultToDelete)
        val deleteVaultIntent = DashboardIntent.DeleteVault
        val expectedCategories = listOf(
            DashboardMockData.getUiCategoryShort(id = "1", vaultsAmount = 3),
            DashboardMockData.getUiCategoryShort(id = "2", vaultsAmount = 2),
        )
        val expectedVaults = listOf(
            DashboardMockData.getVaultShort(id = "2"),
        )

        coEvery {
            vaultRepository.getAll(any(), any(), any())
        } returns DashboardMockData.getVaultsPage(vaults = initialVaults)
        coEvery { categoryRepository.getAllShort() } returns initialCategories

        // when
        viewModel.dispatch(loadCategoriesIntent)
        viewModel.dispatch(loadVaultsIntent)
        viewModel.dispatch(openDeleteDialogIntent)
        viewModel.dispatch(deleteVaultIntent)
        val actualCategories = viewModel.state.value.categories
        val actualVaults = viewModel.state.value.vaults

        // then
        Assert.assertEquals(expectedCategories, actualCategories)
        Assert.assertEquals(expectedVaults, actualVaults)
    }

    @Test
    fun `trigger dismissDeleteDialog updates deleteDialogOpenedForVault correctly`() {
        // given
        val vaultToDelete = DashboardMockData.getVaultShort(id = "1")
        val openDeleteDialogIntent = DashboardIntent.OpenDeleteDialog(vaultToDelete)
        val dismissDeleteDialogIntent = DashboardIntent.DismissDeleteDialog

        val expected = null

        // when
        viewModel.dispatch(openDeleteDialogIntent)
        viewModel.dispatch(dismissDeleteDialogIntent)
        val actual = viewModel.state.value.deleteDialogOpenedForVault

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger openDeleteDialog updates deleteDialogOpenedForVault correctly`() {
        // given
        val vaultToDelete = DashboardMockData.getVaultShort(id = "1")
        val openDeleteDialogIntent = DashboardIntent.OpenDeleteDialog(vaultToDelete)

        val expected = vaultToDelete

        // when
        viewModel.dispatch(openDeleteDialogIntent)
        val actual = viewModel.state.value.deleteDialogOpenedForVault

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `trigger loadNextVaultsPage updates state correctly`() {
        // given
        val loadNextVaultsPageIntent = DashboardIntent.LoadNextVaultsPage
        val mockVaultsPage = DashboardMockData.getVaultsPage(
            vaults = listOf(DashboardMockData.getVaultShort(id = "1")),
            nextPageKey = "2",
            endReached = true,
        )

        val expectedVaults = listOf(DashboardMockData.getVaultShort(id = "1"))
        val expectedNextPageKey = "2"
        val expectedEndReached = true

        coEvery { vaultRepository.getAll(any(), any(), any()) } returns mockVaultsPage

        // when
        viewModel.dispatch(loadNextVaultsPageIntent)
        val actualVaults = viewModel.state.value.vaults
        val actualNextPageKey = viewModel.state.value.nextPageKey
        val actualIsEndReached = viewModel.state.value.isEndReached

        // then
        Assert.assertEquals(expectedVaults, actualVaults)
        Assert.assertEquals(expectedNextPageKey, actualNextPageKey)
        Assert.assertEquals(expectedEndReached, actualIsEndReached)
    }
}