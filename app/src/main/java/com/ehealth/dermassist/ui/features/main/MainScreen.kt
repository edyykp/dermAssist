package com.ehealth.dermassist.ui.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ehealth.dermassist.ui.features.history.HistoryScreen
import com.ehealth.dermassist.ui.features.home.HomeScreen
import com.ehealth.dermassist.ui.features.profile.ProfileScreen
import com.ehealth.dermassist.ui.features.report.ReportScreen
import com.ehealth.dermassist.ui.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items =
        listOf(
            _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Home,
            _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Report,
            _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.History,
            _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Profile,
        )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when (screen) {
                                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen
                                    .Home -> Icon(Icons.Filled.Home, contentDescription = null)
                                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen
                                    .Report -> Icon(Icons.Filled.Info, contentDescription = null)
                                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen
                                    .History -> Icon(Icons.Filled.List, contentDescription = null)
                                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen
                                    .Profile -> Icon(Icons.Filled.Person, contentDescription = null)
                                else -> Icon(Icons.Filled.Home, contentDescription = null)
                            }
                        },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected =
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination =
                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Home.route,
            Modifier.padding(innerPadding),
        ) {
            composable(_root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Home.route) {
                _root_ide_package_.com.ehealth.dermassist.ui.features.home.HomeScreen()
            }
            composable(
                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Report.route
            ) {
                _root_ide_package_.com.ehealth.dermassist.ui.features.report.ReportScreen()
            }
            composable(
                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.History.route
            ) {
                _root_ide_package_.com.ehealth.dermassist.ui.features.history.HistoryScreen()
            }
            composable(
                _root_ide_package_.com.ehealth.dermassist.ui.navigation.Screen.Profile.route
            ) {
                _root_ide_package_.com.ehealth.dermassist.ui.features.profile.ProfileScreen()
            }
        }
    }
}
