package com.ucb.proyectofinalgamerteca.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.proyectofinalgamerteca.MainViewModel
import com.ucb.proyectofinalgamerteca.core.data.RemoteConfigManager
import com.ucb.proyectofinalgamerteca.features.auth.data.repository.FirebaseRepository
import com.ucb.proyectofinalgamerteca.features.games.data.api.IgdbService
import com.ucb.proyectofinalgamerteca.features.games.data.database.GamesRoomDatabase
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesLocalDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.datasource.GamesRemoteDataSource
import com.ucb.proyectofinalgamerteca.features.games.data.repository.GamesRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.repository.IGamesRepository
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetGameDetailsUseCase
import com.ucb.proyectofinalgamerteca.features.games.domain.usecase.GetPopularGamesUseCase
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.DeveloperGamesViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.detail.GameDetailViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.home.GamesListViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.GenreGamesViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.PlatformGamesViewModel
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.lists_public.PublicListsViewModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.filters.ReleaseYearGamesViewModel
import com.ucb.proyectofinalgamerteca.features.login.domain.usecase.LoginUseCase
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginViewModel
import com.ucb.proyectofinalgamerteca.features.settings.presentation.ProfileViewModel
import com.ucb.proyectofinalgamerteca.features.register.presentation.RegisterViewModel
import com.ucb.proyectofinalgamerteca.features.settings.presentation.SettingsViewModel
import com.ucb.proyectofinalgamerteca.features.startupScreen.presentation.StartupViewModel
import com.ucb.proyectofinalgamerteca.features.user_library.data.repository.UserLibraryRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.repository.IUserLibraryRepository
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.AddGameToLibraryUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.AddGameToListUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.AddReviewUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.CreateCustomListUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.GetUserGameInteractionUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.GetUserListsUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.SetUserRatingUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.domain.usecase.ToggleFavoriteUseCase
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.list_detail.ListDetailViewModel
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.my_collection.UserGamesViewModel
import com.ucb.proyectofinalgamerteca.features.user_library.presentation.lists_manager.UserListsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    // =============================================================================================
    // 1. CONFIGURACIÓN Y EXTERNOS (Firebase, Retrofit, OkHttp)
    // =============================================================================================

    // Credenciales IGDB
    single(named("igdb_client_id")) {
        "s5jtkakjst6oe0ngkk8cjb2w45edm0"
    }
    single(named("igdb_access_token")) {
        "dhszlba7qkzsra4iwh1mbtmbhpuuol"
    }

    // Firebase Instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Cliente HTTP
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE
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

    // Servicios API
    single<IgdbService> {
        get<Retrofit>(named("igdb_retrofit")).create(IgdbService::class.java)
    }

    //FirebaseRemoteConfig
    single { RemoteConfigManager() }
    viewModel { MainViewModel(remoteConfigManager = get()) }
    // =============================================================================================
    // 2. BASE DE DATOS LOCAL (Room)
    // =============================================================================================

    single { GamesRoomDatabase.getDatabase(androidContext()) }
    single { get<GamesRoomDatabase>().gameDao() }

    // =============================================================================================
    // 3. FUENTES DE DATOS (Data Sources)
    // =============================================================================================

    single {
        GamesRemoteDataSource(
            service = get(),
            clientId = get(named("igdb_client_id")),
            accessToken = get(named("igdb_access_token"))
        )
    }
    single { GamesLocalDataSource(dao = get()) }

    // =============================================================================================
    // 4. REPOSITORIOS
    // =============================================================================================

    // Repositorio de Juegos
    single<IGamesRepository> {
        GamesRepository(remote = get(), local = get())
    }

    // Repositorio de Librería de Usuario
    single<IUserLibraryRepository> {
        UserLibraryRepository(db = get())
    }

    // Repositorio de Autenticación
    single {
        FirebaseRepository(auth = get(), db = get())
    }

    // =============================================================================================
    // 5. CASOS DE USO (Use Cases)
    // =============================================================================================

    // --- Juegos ---
    single { GetPopularGamesUseCase(repository = get()) }
    single { GetGameDetailsUseCase(repository = get()) }

    // --- Librería de Usuario ---
    factory { AddGameToLibraryUseCase(get()) }
    factory { GetUserGameInteractionUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { SetUserRatingUseCase(get()) }
    factory { CreateCustomListUseCase(get()) }
    factory { GetUserListsUseCase(get()) }
    factory { AddGameToListUseCase(get()) }
    factory { AddReviewUseCase(get()) }

    // --- Auth ---
    factory { LoginUseCase() }

    // =============================================================================================
    // 6. VIEW MODELS
    // =============================================================================================

    // --- Flujo Principal ---
    viewModel { StartupViewModel() }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }

    // --- Perfil y Configuración ---
    viewModel { ProfileViewModel(repo = get()) }
    viewModel { SettingsViewModel(repo = get()) }

    // --- Listas y Biblioteca ---
    viewModel { GamesListViewModel(getPopularGames = get()) }

    // Lista de juegos del usuario (Mis Juegos, Favoritos, etc.)
    viewModel {
        UserGamesViewModel(
            libraryRepository = get(),
            authRepo = get(),
            gamesRepository = get()
        )
    }

    // Listas del usuario
    viewModel {
        PublicListsViewModel(
            libraryRepo = get(),
            gamesRepo = get(),
            authRepo = get(),
            createCustomListUseCase = get()
        )
    }

    viewModel {
        UserListsViewModel(
            getUserLists = get(),
            gamesRepo = get(),
            authRepo = get()
        )
    }

    viewModel {
        ListDetailViewModel(
            libraryRepo = get(),
            gamesRepo = get()
        )
    }

    // Detalle del Juego (¡Completo!)
    viewModel {
        GameDetailViewModel(
            getGameDetails = get(),
            addGameToLibrary = get(),
            getUserInteraction = get(),
            toggleFavorite = get(),
            setUserRating = get(),
            getUserLists = get(),
            createCustomList = get(),
            addGameToList = get(),
            repo = get()
        )
    }

    // --- Filtros Específicos ---
    viewModel { PlatformGamesViewModel(get()) }
    viewModel { GenreGamesViewModel(get()) }
    viewModel { ReleaseYearGamesViewModel(get()) }
    viewModel { DeveloperGamesViewModel(get()) }
}