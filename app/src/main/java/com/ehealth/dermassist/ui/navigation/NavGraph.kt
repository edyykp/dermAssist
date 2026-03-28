package com.ehealth.dermassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehealth.dermassist.ui.features.auth.LoginScreen
import com.ehealth.dermassist.ui.features.main.MainScreen
import com.ehealth.dermassist.ui.features.onboarding.OnboardingScreen
import com.ehealth.dermassist.ui.features.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Splash.route) {
        composable(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Splash.route) {
            _root_ide_package_.com.ehealth.dermassist.ui.features.splash.SplashScreen(
                onContinueWithGoogle = { navController.navigate(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Main.route) },
                onSignUpLogin = { navController.navigate(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Login.route) },
            )
        }
        composable(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Onboarding.route) { _root_ide_package_.com.ehealth.dermassist.ui.features.onboarding.OnboardingScreen() }
        composable(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Login.route) { _root_ide_package_.com.ehealth.dermassist.ui.features.auth.LoginScreen() }
        composable(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Main.route) { _root_ide_package_.com.ehealth.dermassist.ui.features.main.MainScreen() }
    }
}
