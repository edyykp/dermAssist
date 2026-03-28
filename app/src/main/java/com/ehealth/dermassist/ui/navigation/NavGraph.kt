package com.ehealth.dermassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehealth.dermassist.ui.features.auth.AuthViewModel
import com.ehealth.dermassist.ui.features.auth.LoginScreen
import com.ehealth.dermassist.ui.features.main.MainScreen
import com.ehealth.dermassist.ui.features.onboarding.OnboardingScreen
import com.ehealth.dermassist.ui.features.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val user by authViewModel.user.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (user != null) Screen.Main.route else Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignUpLogin = { navController.navigate(Screen.Login.route) },
            )
        }
        composable(Screen.Onboarding.route) { OnboardingScreen() }
        composable(Screen.Login.route) { LoginScreen() }
        composable(Screen.Main.route) { MainScreen() }
    }
}
