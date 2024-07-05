package dev.banger.hootkey.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.banger.hootkey.di.viewmodel.ViewModelKey
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import dev.banger.hootkey.presentation.viewmodel.CategoriesViewModel
import dev.banger.hootkey.presentation.viewmodel.DashboardViewModel
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel
import dev.banger.hootkey.presentation.viewmodel.LoginViewModel
import dev.banger.hootkey.presentation.viewmodel.NewCategoryViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateFieldViewModel
import dev.banger.hootkey.presentation.viewmodel.NewTemplateViewModel
import dev.banger.hootkey.presentation.viewmodel.NewVaultViewModel
import dev.banger.hootkey.presentation.viewmodel.PasswordGeneratorViewModel
import dev.banger.hootkey.presentation.viewmodel.RegisterViewModel
import dev.banger.hootkey.presentation.viewmodel.SettingsViewModel
import dev.banger.hootkey.presentation.viewmodel.StatisticsViewModel
import dev.banger.hootkey.presentation.viewmodel.TemplatesViewModel

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(LaunchViewModel::class)
    abstract fun bindLaunchViewModel(viewModel: LaunchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(viewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoriesViewModel::class)
    abstract fun bindCategoriesViewModel(viewModel: CategoriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewCategoryViewModel::class)
    abstract fun bindNewCategoryViewModel(viewModel: NewCategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewTemplateFieldViewModel::class)
    abstract fun bindNewTemplateFieldViewModel(viewModel: NewTemplateFieldViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewTemplateViewModel::class)
    abstract fun bindNewTemplateViewModel(viewModel: NewTemplateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewVaultViewModel::class)
    abstract fun bindNewVaultViewModel(viewModel: NewVaultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordGeneratorViewModel::class)
    abstract fun bindPasswordGeneratorViewModel(viewModel: PasswordGeneratorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    abstract fun bindStatisticsViewModel(viewModel: StatisticsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TemplatesViewModel::class)
    abstract fun bindTemplatesViewModel(viewModel: TemplatesViewModel): ViewModel
}