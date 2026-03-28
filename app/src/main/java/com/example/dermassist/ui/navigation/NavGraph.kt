package com.example.dermassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dermassist.ui.features.auth.LoginScreen
import com.example.dermassist.ui.features.main.MainScreen
import com.example.dermassist.ui.features.onboarding.OnboardingScreen
import com.example.dermassist.ui.features.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onContinueWithGoogle = { navController.navigate(Screen.Main.route) },
                onSignUpLogin = { navController.navigate(Screen.Login.route) }
            )
        }
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
