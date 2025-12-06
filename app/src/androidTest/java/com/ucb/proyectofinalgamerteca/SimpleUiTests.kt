package com.ucb.proyectofinalgamerteca

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.ucb.proyectofinalgamerteca.features.games.domain.model.GameModel
import com.ucb.proyectofinalgamerteca.features.games.presentation.explore.GamesListScreen
import com.ucb.proyectofinalgamerteca.features.games.presentation.explore.GamesListViewModel
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginScreen
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginUiState
import com.ucb.proyectofinalgamerteca.features.login.presentation.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SimpleUiTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // -------------------------------------------------------------------------
    // PRUEBA 1: PANTALLA DE LOGIN
    // Verificamos que se muestren los campos y se pueda escribir
    // -------------------------------------------------------------------------
    @Test
    fun loginScreen_showsFields_and_acceptsTyping() {
        // 1. MOCK (Simulación): Creamos un ViewModel falso
        // "relaxed = true" significa que si llamamos a algo no definido, no explote.
        val mockViewModel = mockk<LoginViewModel>(relaxed = true)

        // 2. COMPORTAMIENTO: Le decimos al Mock qué estado tener.
        // Simulamos un estado inicial limpio.
        val fakeState = MutableStateFlow(LoginUiState())
        every { mockViewModel.uiState } returns fakeState

        // 3. CARGAR PANTALLA: Pasamos nuestro 'mockViewModel' explícitamente.
        // Al pasarlo aquí, Compose IGNORA el 'koinViewModel()' de tu código original.
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {},
                viewModel = mockViewModel
            )
        }

        // 4. VERIFICACIONES (Asserts)

        // Verificar que el título existe
        composeTestRule.onNodeWithText("Iniciar sesión").assertIsDisplayed()

        // Verificar campo de correo y escribir en él
        composeTestRule.onNodeWithText("Correo electrónico")
            .assertIsDisplayed()
            .performTextInput("test@gamerteca.com")

        // Verificar campo de contraseña y escribir
        composeTestRule.onNodeWithText("Contraseña")
            .assertIsDisplayed()
            .performTextInput("123456")
    }

    // -------------------------------------------------------------------------
    // PRUEBA 2: PANTALLA DE LISTA DE JUEGOS (GAMES LIST)
    // Verificamos que si el ViewModel trae juegos, la lista los muestra
    // -------------------------------------------------------------------------
    @Test
    fun gamesListScreen_showsGames_whenSuccess() {
        // 1. MOCK: Creamos el VM falso
        val mockViewModel = mockk<GamesListViewModel>(relaxed = true)

        // 2. DATOS FALSOS: Creamos un juego de prueba
        // Nota: Asegúrate de que tu GameModel tenga estos campos o ajusta el constructor
        val fakeGame = GameModel(
            id = 1L,
            name = "Super Kong Adventure",
            coverUrl = "https://placeholder.com/img.png",
            rating = 95.0,
            summary = "Un juego de prueba",
            releaseDate = 123456789L,
            platforms = listOf(),
            genres = listOf(),

        )

        // 3. ESTADO: Forzamos al VM a decir "Ya cargué con éxito" (Success)
        val successState = GamesListViewModel.UiState(
            games = listOf(fakeGame),
            isLoading = false,
            error = null
        )
        every { mockViewModel.uiState } returns MutableStateFlow(successState)

        // 4. CARGAR PANTALLA
        composeTestRule.setContent {
            GamesListScreen(
                onGameClick = {},
                vm = mockViewModel
            )
        }

        // 5. VERIFICAR
        // Buscamos si el texto del juego aparece en pantalla
        composeTestRule.onNodeWithText("Super Kong Adventure").assertExists()

        // Verificamos que NO aparezca el error ni el loading
        // (Esto es opcional, pero buena práctica)
        // composeTestRule.onNodeWithText("Oops!").assertDoesNotExist()
    }

    // -------------------------------------------------------------------------
    // PRUEBA 3: LOGIN - ESTADO DE CARGA
    // Verificamos que si isLoading es true, el botón cambia o se bloquea
    // -------------------------------------------------------------------------
    @Test
    fun loginScreen_showsLoadingState() {
        val mockViewModel = mockk<LoginViewModel>(relaxed = true)

        // Simulamos que el usuario dio click y está cargando
        val loadingState = MutableStateFlow(LoginUiState(isLoading = true))
        every { mockViewModel.uiState } returns loadingState

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {},
                viewModel = mockViewModel
            )
        }

        // Buscamos el botón. En tu código, cuando isLoading = true,
        // el texto "Continuar" desaparece y sale un CircularProgressIndicator.
        // Por tanto, verificar que "Continuar" NO existe es una forma válida de probarlo.

        // Ojo: Compose a veces mantiene el nodo en el árbol semántico aunque no se pinte.
        // Pero en tu código el 'if(isLoading)' reemplaza el texto.

        // Opción A: Verificar que NO está el texto "Continuar" dentro del botón
        // (Esto depende de cómo Compose maneje el árbol, pero intentemos verificar el botón deshabilitado)

        // Tu botón tiene: enabled = !uiState.isLoading
        // Vamos a buscar el botón que contiene el indicador de carga (o buscar por texto si falla)

        // Una forma segura es ver que NO sea clickeable o buscar un nodo padre.
        // Pero para hacerlo simple, verifiquemos que el campo de texto sigue ahí
        // (la pantalla no crasheó) y que no se puede interactuar con el botón "Continuar" si tuviera texto.

        // Como quitaste el texto "Continuar" en el loading, esta aserción DEBE pasar:
        composeTestRule.onNodeWithText("Continuar").assertDoesNotExist()
    }
}