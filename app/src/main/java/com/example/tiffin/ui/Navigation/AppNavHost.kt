package com.example.tiffin.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tiffin.analytics.Analytics
import com.example.tiffin.analytics.LogcatAnalytics
import com.example.tiffin.data.repository.KitchenRepository
import com.example.tiffin.ui.screens.HomeScreen
import com.example.tiffin.ui.screens.KitchenDetailsScreen
import com.example.tiffin.ui.screens.PaywallScreen
import com.example.tiffin.ui.screens.PaywallStore
import com.example.tiffin.ui.viewModel.KitchenViewModel
import com.example.tiffin.ui.viewModel.KitchenViewModelFactory
import com.example.tiffin.ui.viewModel.PaywallViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    // Analytics instance
    val analytics: Analytics = remember { LogcatAnalytics() }

    val kitchenVmFactory = remember {
        KitchenViewModelFactory(KitchenRepository(context))
    }
    val kitchenViewModel: KitchenViewModel = viewModel(factory = kitchenVmFactory)

    val paywallVm: PaywallViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                PaywallViewModel(
                    store = PaywallStore(context),
                    analytics = analytics,
                    handle = createSavedStateHandle()
                )
            }
        }
    )

    val paywall by paywallVm.state.collectAsStateWithLifecycle()
    val isPaid = paywall?.paid == true

    // Requirement: opens on the 3rd launch and on any subscribe tap.
    // Persistent autoPaywallShown check ensures it only triggers once automatically.
    LaunchedEffect(paywall) {
        val s = paywall ?: return@LaunchedEffect
        if (s.launchCount >= 3 && !s.autoPaywallShown && !s.paid) {
            paywallVm.markAutoPaywallShown()
            analytics.logEvent(
                "paywall_shown",
                mapOf("trigger_source" to "auto_3rd_launch", "launch_count" to s.launchCount)
            )
            navController.navigate("paywall")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = kitchenViewModel,
                analytics = analytics,
                isSubscribed = isPaid,
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
            val kitchenId = backStackEntry.arguments?.getInt("kitchenId") ?: -1

            KitchenDetailsScreen(
                kitchenId = kitchenId,
                viewModel = kitchenViewModel,
                analytics = analytics,
                isSubscribed = isPaid,
                onBackClick = {
                    navController.popBackStack()
                },
                onSubscribeClick = { kId ->
                    if (isPaid) {
                        Toast.makeText(context, "You're already subscribed to Tiffin Plus!", Toast.LENGTH_SHORT).show()
                    } else {
                        analytics.logEvent(
                            "paywall_shown",
                            mapOf("trigger_source" to "subscribe_button", "kitchen_id" to kId)
                        )
                        navController.navigate("paywall") {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable("paywall") {
            PaywallScreen(
                onPurchase = {
                    paywallVm.purchase {
                        Toast.makeText(context, "Welcome to Tiffin Plus! Subscription Active.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}
