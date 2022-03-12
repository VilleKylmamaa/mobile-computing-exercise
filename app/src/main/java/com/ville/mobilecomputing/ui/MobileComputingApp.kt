package com.ville.mobilecomputing.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ville.mobilecomputing.ui.home.Home
import com.ville.mobilecomputing.ui.home.categoryPayment.pieChart.PieChart
import com.ville.mobilecomputing.ui.login.Login
import com.ville.mobilecomputing.ui.maps.PaymentLocationMap
import com.ville.mobilecomputing.ui.payment.Payment

@Composable
fun MobileComputingApp(
    appState: MobileComputingAppState = rememberMobileComputingAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            Login(navController = appState.navController)
        }
        composable(route = "home") {
            Home(navController = appState.navController)
        }
        composable(route = "payment") {
            Payment(onBackPress = appState::navigateBack, navController = appState.navController)
        }
        composable(route = "map") {
            PaymentLocationMap(navController = appState.navController)
        }
        composable(
            route = "pieChart/{categoryId}",
            arguments = listOf(navArgument("categoryId") {type = NavType.IntType})
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("categoryId")?.let { categoryId ->
                PieChart(
                    onBackPress = appState::navigateBack,
                    categoryId.toLong()
                )
            }
        }

    }
}