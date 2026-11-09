package com.example.tiffin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tiffin.data.repository.KitchenRepository
import com.example.tiffin.ui.screens.HomeScreen
import com.example.tiffin.ui.screens.KitchenDetailsScreen
import com.example.tiffin.ui.viewModel.KitchenViewModel
import com.example.tiffin.ui.viewModel.KitchenViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val factory = remember {
        KitchenViewModelFactory(
            KitchenRepository(context)
        )
    }
    val kitchenViewModel: KitchenViewModel = viewModel(
        factory = factory
    )
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = kitchenViewModel,
                onKitchenClick = { kitchenId ->
                    navController.navigate("kitchen/$kitchenId")
                }
            )
        }
        composable(
            route = "kitchen/{kitchenId}",
            arguments = listOf(
                navArgument("kitchenId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val kitchenId = backStackEntry.arguments
                ?.getInt("kitchenId") ?: -1

            KitchenDetailsScreen(
                kitchenId = kitchenId,
                viewModel = kitchenViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}