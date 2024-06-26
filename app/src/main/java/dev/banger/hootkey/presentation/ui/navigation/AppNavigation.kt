package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.banger.hootkey.Constants.ADDED_VAULT_CATEGORIES_KEY
import dev.banger.hootkey.Constants.CATEGORY_ICON_KEY
import dev.banger.hootkey.Constants.CREATED_CATEGORY_ID_KEY
import dev.banger.hootkey.Constants.CREATED_TEMPLATE_ID_KEY
import dev.banger.hootkey.Constants.DELETED_VAULT_CATEGORIES_KEY
import dev.banger.hootkey.Constants.DELETED_VAULT_IDS_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_NEW_CATEGORY_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_OLD_CATEGORY_KEY
import dev.banger.hootkey.Constants.TEMPLATE_KEY
import dev.banger.hootkey.Constants.UPDATED_VAULT_IDS_KEY
import dev.banger.hootkey.Constants.VAULT_CATEGORY_KEY
import dev.banger.hootkey.Constants.VAULT_KEY
import dev.banger.hootkey.presentation.ui.navigation.NavigationDestinations.NULL_ARG_VALUE
import dev.banger.hootkey.presentation.ui.screen.auth.AccountAuthScreen
import dev.banger.hootkey.presentation.ui.screen.auth.AuthScreen
import dev.banger.hootkey.presentation.ui.screen.categories.CategoriesScreen
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardScreen
import dev.banger.hootkey.presentation.ui.screen.edit_vault.EditVaultScreen
import dev.banger.hootkey.presentation.ui.screen.launch.LaunchScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.CategoryIconsScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.NewCategoryScreen
import dev.banger.hootkey.presentation.ui.screen.new_template.NewTemplateScreen
import dev.banger.hootkey.presentation.ui.screen.new_vault.NewVaultScreen
import dev.banger.hootkey.presentation.ui.screen.settings.SettingsScreen
import dev.banger.hootkey.presentation.ui.screen.statistics.StatisticsScreen
import dev.banger.hootkey.presentation.ui.screen.templates.TemplatesScreen
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListScreen

@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationDestinations.LAUNCH
    ) {
        composable(NavigationDestinations.LAUNCH) {
            LaunchScreen(onNavigateToAccountLogin = {
                navHostController.clearBackStackAndNavigate(NavigationDestinations.ACCOUNT_LOGIN)
            }, onNavigateToLogin = {
                navHostController.clearBackStackAndNavigate(NavigationDestinations.LOGIN)
            })
        }
        composable(NavigationDestinations.ACCOUNT_LOGIN) {
            AccountAuthScreen(isLogin = true, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.ACCOUNT_REGISTRATION)
            }, onSuccess = {
                navHostController.clearBackStackAndNavigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable(NavigationDestinations.ACCOUNT_REGISTRATION) {
            AccountAuthScreen(isLogin = false, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.ACCOUNT_LOGIN)
            }, onSuccess = {
                navHostController.clearBackStackAndNavigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable(NavigationDestinations.LOGIN) {
            AuthScreen(onSuccess = {
                navHostController.clearBackStackAndNavigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable(NavigationDestinations.DASHBOARD) {
            DashboardScreen(
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                }, onAddNewVault = {
                    navHostController.navigate(NavigationDestinations.NEW_VAULT)
                }, onCategorySelected = { id, name ->
                    navHostController.navigate("${NavigationDestinations.VAULTS}/${id ?: NULL_ARG_VALUE}/${name ?: NULL_ARG_VALUE}")
                }, onEditClick = { id ->
                    navHostController.navigate("${NavigationDestinations.EDIT_VAULT}/$id")
                }, onSettingsClick = {
                    navHostController.navigate(NavigationDestinations.SETTINGS)
                }, onStatisticsClick = {
                    navHostController.navigate(NavigationDestinations.STATISTICS)
                })
        }
        composable("${NavigationDestinations.VAULTS}/{${NavigationDestinations.VAULT_CATEGORY_ID_ARG}}/{${NavigationDestinations.VAULT_CATEGORY_NAME_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.VAULT_CATEGORY_ID_ARG) {
                    type = NavType.StringType
                },
                navArgument(NavigationDestinations.VAULT_CATEGORY_NAME_ARG) {
                    type = NavType.StringType
                }
            )) { backStackEntry ->
            val categoryId =
                backStackEntry.arguments?.getString(NavigationDestinations.VAULT_CATEGORY_ID_ARG)
                    .takeIf { it != NULL_ARG_VALUE }
            val categoryName =
                backStackEntry.arguments?.getString(NavigationDestinations.VAULT_CATEGORY_NAME_ARG)
                    .takeIf { it != NULL_ARG_VALUE }
            VaultsListScreen({
                navHostController.currentBackStackEntry?.savedStateHandle
            }, categoryName, categoryId, { id ->
                navHostController.navigate("${NavigationDestinations.EDIT_VAULT}/$id")
            }, { deletedVaultIds, updatedVaultIds, deletedVaultCategories, addedVaultCategories ->
                navHostController.popBackStack()
                if (deletedVaultIds.isNotEmpty()) {
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(DELETED_VAULT_IDS_KEY, deletedVaultIds)
                }
                if (updatedVaultIds.isNotEmpty()) {
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(UPDATED_VAULT_IDS_KEY, updatedVaultIds)
                }
                if (deletedVaultCategories.isNotEmpty()) {
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(DELETED_VAULT_CATEGORIES_KEY, deletedVaultCategories)
                }
                if (addedVaultCategories.isNotEmpty()) {
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(ADDED_VAULT_CATEGORIES_KEY, addedVaultCategories)
                }
            })
        }
        composable(NavigationDestinations.NEW_VAULT) {
            NewVaultScreen(
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onNavigateToCategories = {
                    navHostController.navigate(NavigationDestinations.CATEGORIES)
                }, onSuccess = { vaultId, categoryId ->
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(VAULT_KEY, vaultId)
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(VAULT_CATEGORY_KEY, categoryId)
                }
            )
        }

        composable(NavigationDestinations.NEW_TEMPLATE) {
            NewTemplateScreen(
                onSuccess = {
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(CREATED_TEMPLATE_ID_KEY, it)
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(NavigationDestinations.NEW_CATEGORY) {
            NewCategoryScreen(
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onNavigateToTemplates = {
                    navHostController.navigate(NavigationDestinations.TEMPLATES)
                },
                onNavigateToIcons = {
                    navHostController.navigate(NavigationDestinations.CATEGORY_ICONS)
                },
                onSuccess = {
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(CREATED_CATEGORY_ID_KEY, it)
                }
            )
        }

        composable(NavigationDestinations.CATEGORY_ICONS) {
            CategoryIconsScreen(
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onChooseIcon = {
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(CATEGORY_ICON_KEY, it.name)
                }
            )
        }

        composable(NavigationDestinations.CATEGORIES) {
            CategoriesScreen(
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onCreateCategoryClick = {
                    navHostController.navigate(NavigationDestinations.NEW_CATEGORY)
                },
                onChooseCategory = {
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(CREATED_CATEGORY_ID_KEY, it.id)
                }
            )
        }

        composable(NavigationDestinations.TEMPLATES) {
            TemplatesScreen(
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onChooseTemplate = {
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(TEMPLATE_KEY, it)
                },
                onCreateTemplateClick = {
                    navHostController.navigate(NavigationDestinations.NEW_TEMPLATE)
                }
            )
        }

        composable(
            route = "${NavigationDestinations.EDIT_VAULT}/{${NavigationDestinations.VAULT_ID_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.VAULT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val vaultId = backStackEntry.arguments?.getString(NavigationDestinations.VAULT_ID_ARG)
                ?: throw IllegalStateException("could not get vaultId for EditVault")

            EditVaultScreen(
                vaultId = vaultId,
                savedStateHandleProvider = {
                    navHostController.currentBackStackEntry?.savedStateHandle
                },
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onSuccess = { successInfo ->
                    navHostController.popBackStack()
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(EDITED_VAULT_KEY, successInfo.vaultId)
                    successInfo.newCategoryId?.let {
                        navHostController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(EDITED_VAULT_NEW_CATEGORY_KEY, it)
                    }
                    successInfo.oldCategoryId?.let {
                        navHostController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(EDITED_VAULT_OLD_CATEGORY_KEY, it)
                    }
                },
                onNavigateToCategories = {
                    navHostController.navigate(NavigationDestinations.CATEGORIES)
                }
            )
        }

        composable(NavigationDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onLogout = {
                    navHostController.clearBackStackAndNavigate(NavigationDestinations.ACCOUNT_LOGIN)
                }
            )
        }

        composable(NavigationDestinations.STATISTICS) {
            StatisticsScreen(onNavigateBack = {
                navHostController.popBackStack()
            })
        }
    }
}

fun NavHostController.clearBackStackAndNavigate(destination: String) {
    navigate(destination) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}