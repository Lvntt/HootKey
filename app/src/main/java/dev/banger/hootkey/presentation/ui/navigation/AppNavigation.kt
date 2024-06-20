package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardScreen
import dev.banger.hootkey.Constants.CATEGORY_ICON_KEY
import dev.banger.hootkey.Constants.CREATED_CATEGORY_ID_KEY
import dev.banger.hootkey.Constants.CREATED_TEMPLATE_ID_KEY
import dev.banger.hootkey.Constants.TEMPLATE_KEY
import dev.banger.hootkey.domain.entity.category.CategoryIcon
import dev.banger.hootkey.domain.entity.category.CreateCategoryRequest
import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.FieldType
import dev.banger.hootkey.domain.entity.template.TemplateField
import dev.banger.hootkey.domain.entity.vault.CreateVaultRequest
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.ui.screen.TestScreen
import dev.banger.hootkey.presentation.ui.screen.auth.AccountAuthScreen
import dev.banger.hootkey.presentation.ui.screen.auth.AuthScreen
import dev.banger.hootkey.presentation.ui.screen.categories.CategoriesScreen
import dev.banger.hootkey.presentation.ui.screen.launch.LaunchScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.CategoryIconsScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.NewCategoryScreen
import dev.banger.hootkey.presentation.ui.screen.new_template.NewTemplateScreen
import dev.banger.hootkey.presentation.ui.screen.new_vault.NewVaultScreen
import dev.banger.hootkey.presentation.ui.screen.templates.TemplatesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationDestinations.LAUNCH
    ) {
        composable(NavigationDestinations.LAUNCH) {
            LaunchScreen(onNavigateToAccountLogin = {
                navHostController.navigate(NavigationDestinations.ACCOUNT_LOGIN)
            }, onNavigateToLogin = {
                navHostController.navigate(NavigationDestinations.LOGIN)
            })
        }
        composable(NavigationDestinations.ACCOUNT_LOGIN) {
            AccountAuthScreen(isLogin = true, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.ACCOUNT_REGISTRATION)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable(NavigationDestinations.ACCOUNT_REGISTRATION) {
            AccountAuthScreen(isLogin = false, onNavigateFromBottomHint = {
                navHostController.navigate(NavigationDestinations.ACCOUNT_LOGIN)
            }, onSuccess = {
                navHostController.navigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable(NavigationDestinations.LOGIN) {
            AuthScreen(onSuccess = {
                navHostController.navigate(NavigationDestinations.DASHBOARD)
            })
        }
        composable<NavigationDestinations.Dashboard> {
            DashboardScreen()
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
        //TODO FOR TESTING PURPOSES ONLY
        composable(NavigationDestinations.PASSWORD_GENERATOR) {
            TestScreen()
        }
        //-------------------------
    }
}