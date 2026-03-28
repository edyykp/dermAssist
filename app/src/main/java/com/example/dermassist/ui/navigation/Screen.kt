package com.example.dermassist.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Main : Screen("main")
    
    // Bottom tab routes
    object Home : Screen("home")
    object Report : Screen("report")
    object History : Screen("history")
    object Profile : Screen("profile")
}
