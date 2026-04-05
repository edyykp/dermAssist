package com.ehealth.dermassist.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehealth.dermassist.ui.features.auth.AuthViewModel
import com.ehealth.dermassist.ui.features.auth.LoginScreen
import com.ehealth.dermassist.ui.features.legal.PrivacyPolicyScreen
import com.ehealth.dermassist.ui.features.legal.TermsAndConditionsScreen
import com.ehealth.dermassist.ui.features.main.MainScreen
import com.ehealth.dermassist.ui.features.onboarding.OnboardingScreen
import com.ehealth.dermassist.ui.features.profile.editprofile.EditProfileScreen
import com.ehealth.dermassist.ui.features.splash.SplashScreen
import com.ehealth.dermassist.ui.theme.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn == null) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.background,
                                    )
                            )
                    ),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn == true) Screen.Main.route else Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignUpLogin = { navController.navigate(Screen.Login.route) },
                onTermsClick = { navController.navigate(Screen.TermsAndConditions.route) },
                onPrivacyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
            )
        }
        composable(Screen.Onboarding.route) { OnboardingScreen() }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Main.route) {
            MainScreen(onEditProfileClick = { navController.navigate(Screen.EditProfile.route) })
        }
        composable(Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(onCancel = { navController.popBackStack() })
        }
    }
}
