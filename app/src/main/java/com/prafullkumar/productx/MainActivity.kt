package com.prafullkumar.productx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prafullkumar.productx.ui.screens.addProduct.AddProductScreen
import com.prafullkumar.productx.ui.screens.productListing.ProductListingScreen
import com.prafullkumar.productx.ui.theme.ProductXTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


/**
 * Sealed interface representing the different screens in the application.
 */
sealed interface Screens {
    /**
     * Represents the home screen.
     */
    @Serializable
    data object HomeScreen

    /**
     * Represents the add product screen.
     */
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
    val snackBarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(snackBarHost)
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screens.HomeScreen> {
                ProductListingScreen(navController, getViewModel())
            }
            composable<Screens.AddProductScreen> {
                AddProductScreen(getViewModel(), onDismiss = navController::popBackStack) {
                    navController.popBackStack()
                    scope.launch {
                        snackBarHost.showSnackbar("Product added successfully")
                    }
                }
            }
        }
    }
}