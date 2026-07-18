package com.ejemplo.biblioteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ejemplo.biblioteca.ui.screens.PantallaFormulario
import com.ejemplo.biblioteca.ui.screens.PantallaPrincipal
import com.ejemplo.biblioteca.ui.viewmodel.LibroViewModel

// === PALETA DE COLORES "BIBLIOTECA ROSA" ===
val WarmBrown = Color(0xFFC2607F)      // Rosa Empolvado (Color Principal)
val DeepWood = Color(0xFF5C2A3A)       // Rosa Vino Oscuro (textos/contraste)
val WarmBeige = Color(0xFFFCEEF2)      // Rosa Pastel Suave (Fondo)
val AccentGold = Color(0xFFE8A0B4)     // Rosa Coral (Detalles/Acentos)
val SoftSage = Color(0xFFF6DCE2)       // Rosa Claro (Contenedores secundarios)

private val BibliotecaLightColorScheme = lightColorScheme(
    primary = WarmBrown,
    onPrimary = Color.White,
    secondary = AccentGold,
    background = WarmBeige,
    surface = Color.White,
    surfaceVariant = SoftSage,
    onSurface = DeepWood
)

class MainActivity : ComponentActivity() {
    private val viewModel: LibroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = BibliotecaLightColorScheme
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "principal") {
                        composable("principal") {
                            PantallaPrincipal(
                                viewModel = viewModel,
                                onNavigateToFormulario = { id ->
                                    if (id != null) {
                                        navController.navigate("formulario?id=$id")
                                    } else {
                                        navController.navigate("formulario")
                                    }
                                }
                            )
                        }
                        composable(
                            route = "formulario?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val idStr = backStackEntry.arguments?.getString("id")
                            val id = idStr?.toIntOrNull()
                            PantallaFormulario(
                                viewModel = viewModel,
                                libroId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}