package com.ucb.proyectofinalgamerteca.di

import com.ucb.proyectofinalgamerteca.features.games.data.api.IgdbService
import com.ucb.proyectofinalgamerteca.features.games.data.database.GamesRoomDatabase
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesLocalDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesRemoteDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.repository.GamesRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetGameDetailsUseCase
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetPopularGamesUseCase
import com.ucb.proyectofinalgamerteca.features.games.presentation.GameDetailViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.GamesListViewModel
import com.ucb.proyectofinalgamerteca.features.login.domain.usecase.LoginUseCase
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginViewModel
import com.ucb.proyectofinalgamerteca.features.register.presentation.RegisterViewModel
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    // Configuración
    single(named("igdb_client_id")) {
        "s5jtkakjst6oe0ngkk8cjb2w45edm0"
    }
    single(named("igdb_access_token")) {
        "dhszlba7qkzsra4iwh1mbtmbhpuuol"
    }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Retrofit
    single<Retrofit>(named("igdb_retrofit")) {
        Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Service
    single<IgdbService> {
        get<Retrofit>(named("igdb_retrofit")).create(IgdbService::class.java)
    }

    // Database
    single { GamesRoomDatabase.getDatabase(androidContext()) }
    single { get<GamesRoomDatabase>().gameDao() }

    // Data Sources
    single {
        GamesRemoteDataSource(
            service = get(),
            clientId = get(named("igdb_client_id")),
            accessToken = get(named("igdb_access_token"))
        )
    }
    single { GamesLocalDataSource(dao = get()) }

    // Repository
    single<IGamesRepository> {
        GamesRepository(remote = get(), local = get())
    }

    // Use Cases
    single { GetPopularGamesUseCase(repository = get()) }
    single { GetGameDetailsUseCase(repository = get()) }

    // ViewModels
    viewModel { GamesListViewModel(getPopularGames = get()) }
    viewModel { GameDetailViewModel(getGameDetails = get()) }
    viewModel { StartupViewModel() }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel() }

    factory { LoginUseCase() }
}