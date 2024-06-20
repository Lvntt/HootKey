package dev.banger.hootkey.presentation.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.banger.hootkey.Constants.CATEGORY_ICON_KEY
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
import dev.banger.hootkey.presentation.ui.screen.launch.LaunchScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.CategoryIconsScreen
import dev.banger.hootkey.presentation.ui.screen.new_category.NewCategoryScreen
import dev.banger.hootkey.presentation.ui.screen.new_template.NewTemplateScreen
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
        composable(NavigationDestinations.DASHBOARD) {
            //TODO FOR TESTING PURPOSES ONLY
            val templateRepo = koinInject<TemplateRepository>()
            val categoryRepo = koinInject<CategoryRepository>()
            val vaultRepo = koinInject<VaultRepository>()
            var templateText by remember { mutableStateOf("") }
            var categoryText by remember { mutableStateOf("") }
            var vaultText by remember { mutableStateOf("") }
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())) {
                Button(onClick = {
                    navHostController.navigate(NavigationDestinations.PASSWORD_GENERATOR)
                }) {
                    Text("Password generator")
                }
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        templateRepo.create(
                            CreateTemplateRequest(
                                name = "Account", fields = listOf(
                                    TemplateField(
                                        index = 0, name = "Login", type = FieldType.LOGIN
                                    ), TemplateField(
                                        index = 1, name = "Password", type = FieldType.PASSWORD
                                    )
                                )
                            )
                        )
                    }
                }) {
                    Text("Create template")
                }

                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        templateText = templateRepo.getAllFull().firstOrNull().toString()
                    }
                }) {
                    Text("Get first template")
                }

                Text(templateText)

                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryRepo.create(
                            CreateCategoryRequest(
                                icon = CategoryIcon.SOCIAL_MEDIA,
                                name = "Social media",
                                templateId = templateRepo.getAllShort().first().id
                            )
                        )
                    }
                }) {
                    Text("Create category")
                }

                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryText = categoryRepo.getAllShort().firstOrNull().toString()
                    }
                }) {
                    Text("Get first category")
                }

                Text(categoryText)

                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        vaultText = vaultRepo.create(
                            CreateVaultRequest(
                                categoryId = categoryRepo.getAllShort().first().id,
                                name = "GitHub",
                                fieldValues = mapOf(0 to "SecretLogin", 1 to "SecretPassword")
                            )
                        ).toString()
                    }
                }) {
                    Text("Create vault")
                }

                Text(vaultText)

                Button(
                    onClick = {
                        navHostController.navigate(NavigationDestinations.NEW_CATEGORY)
                    }
                ) {
                    Text("New category")
                }
            }
            //-------------------------
        }
        composable(NavigationDestinations.NEW_TEMPLATE) {
            NewTemplateScreen(
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
        composable(NavigationDestinations.TEMPLATES) {
            TemplatesScreen(
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