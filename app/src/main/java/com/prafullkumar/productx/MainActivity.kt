package com.prafullkumar.productx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prafullkumar.productx.ui.screens.addProduct.AddProductScreen
import com.prafullkumar.productx.ui.screens.productListing.ProductListingScreen
import com.prafullkumar.productx.ui.theme.ProductXTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


sealed interface Screens {
    @Serializable
    data object HomeScreen

    @Serializable
    data object AddProductScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductXTheme {
                NavGraph()
            }
        }
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.HomeScreen) {
        composable<Screens.HomeScreen> {
            ProductListingScreen(navController, getViewModel())
        }
        composable<Screens.AddProductScreen> {
            AddProductScreen(navController, getViewModel())
        }
    }
}