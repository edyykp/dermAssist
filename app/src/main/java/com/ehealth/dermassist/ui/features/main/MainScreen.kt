package com.ehealth.dermassist.ui.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
    val items = listOf(Screen.Home, Screen.Report, Screen.History, Screen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when (screen) {
                                Screen.Home ->
                                    if (currentDestination == Screen.Home)
                                        Icon(Icons.Filled.Home, contentDescription = null)
                                    else Icon(Icons.Outlined.Home, contentDescription = null)
                                Screen.Report ->
                                    if (currentDestination == Screen.Report)
                                        Icon(
                                            Icons.Filled.DocumentScanner,
                                            contentDescription = null,
                                        )
                                    else
                                        Icon(
                                            Icons.Outlined.DocumentScanner,
                                            contentDescription = null,
                                        )

                                Screen.History ->
                                    if (currentDestination == Screen.History)
                                        Icon(Icons.Filled.Folder, contentDescription = null)
                                    else Icon(Icons.Outlined.Folder, contentDescription = null)
                                Screen.Profile ->
                                    if (currentDestination == Screen.Profile)
                                        Icon(Icons.Filled.Person, contentDescription = null)
                                    else Icon(Icons.Outlined.Person, contentDescription = null)

                                else ->
                                    if (currentDestination == Screen.Home)
                                        Icon(Icons.Filled.Home, contentDescription = null)
                                    else Icon(Icons.Outlined.Home, contentDescription = null)
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
                        colors =
                            NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Report.route) { ReportScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
