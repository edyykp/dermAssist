package com.example.dermassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dermassist.ui.features.auth.LoginScreen
import com.example.dermassist.ui.features.main.MainScreen
import com.example.dermassist.ui.features.onboarding.OnboardingScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen()
        }
        composable(Screen.Login.route) {
            LoginScreen()
        }
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}
