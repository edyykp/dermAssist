package com.ehealth.dermassist.ui.navigation

sealed class Screen(val route: String) {
    object Splash : com.ehealth.dermassist.ui.navigation.Screen("splash")

    object Onboarding : com.ehealth.dermassist.ui.navigation.Screen("onboarding")

    object Login : com.ehealth.dermassist.ui.navigation.Screen("login")

    object Main : com.ehealth.dermassist.ui.navigation.Screen("main")

    // Bottom tab routes
    object Home : com.ehealth.dermassist.ui.navigation.Screen("home")

    object Report : com.ehealth.dermassist.ui.navigation.Screen("report")

    object History : com.ehealth.dermassist.ui.navigation.Screen("history")

    object Profile : com.ehealth.dermassist.ui.navigation.Screen("profile")
}
