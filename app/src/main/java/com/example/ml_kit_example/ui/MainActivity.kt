package com.example.ml_kit_example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ml_kit_example.ui.theme.ML_kit_exampleTheme
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.screens.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ML_kit_exampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyAppNavHost()
                }
            }
        }
    }
}

@Composable
fun MyAppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable(
            "camera_screen?title={title}&content={content}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType; nullable = true },
                navArgument("content") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            val content = backStackEntry.arguments?.getString("content")
            MainScreen(navController, title, content)
        }
    }
}