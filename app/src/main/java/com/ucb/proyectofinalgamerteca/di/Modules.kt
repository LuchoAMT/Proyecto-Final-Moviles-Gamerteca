package com.ucb.proyectofinalgamerteca.di

import com.ucb.proyectofinalgamerteca.features.login.domain.usecase.LoginUseCase
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginViewModel
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { StartupViewModel() }
    viewModel { LoginViewModel(get()) }

//    factory { OnContinue }
    factory { LoginUseCase() }
}