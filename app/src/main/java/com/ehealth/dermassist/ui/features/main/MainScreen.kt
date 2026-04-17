package com.ehealth.dermassist.ui.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.ui.MainViewModel
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.features.history.HistoryScreen
import com.ehealth.dermassist.ui.features.history.ScanDetailScreen
import com.ehealth.dermassist.ui.features.home.HomeScreen
import com.ehealth.dermassist.ui.features.profile.ProfileScreen
import com.ehealth.dermassist.ui.features.report.ReportScreen
import com.ehealth.dermassist.ui.navigation.Screen

@Composable
fun MainScreen(
    user: User? = null,
    onEditProfileClick: () -> Unit,
    onPrivacyAndDataClick: () -> Unit,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Report, Screen.History, Screen.Profile)
    val isLoading by mainViewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        val isSelected =
                            when (screen) {
                                Screen.History ->
                                    currentDestination?.hierarchy?.any {
                                        it.route == Screen.History.route ||
                                            it.route == Screen.ScanDetail.route
                                    } == true
                                else ->
                                    currentDestination?.hierarchy?.any {
                                        it.route == screen.route
                                    } == true
                            }

                        NavigationBarItem(
                            icon = {
                                when (screen) {
                                    Screen.Home ->
                                        if (isSelected)
                                            Icon(Icons.Filled.Home, contentDescription = null)
                                        else Icon(Icons.Outlined.Home, contentDescription = null)
                                    Screen.Report ->
                                        if (isSelected)
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
                                        if (isSelected)
                                            Icon(Icons.Filled.Folder, contentDescription = null)
                                        else Icon(Icons.Outlined.Folder, contentDescription = null)
                                    Screen.Profile ->
                                        if (isSelected)
                                            Icon(Icons.Filled.Person, contentDescription = null)
                                        else Icon(Icons.Outlined.Person, contentDescription = null)

                                    else ->
                                        if (isSelected)
                                            Icon(Icons.Filled.Home, contentDescription = null)
                                        else Icon(Icons.Outlined.Home, contentDescription = null)
                                }
                            },
                            label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            enabled = !isLoading,
                            colors =
                                NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
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
                composable(Screen.Home.route) {
                    HomeScreen(
                        user = user,
                        onScanSuccess = {
                            navController.navigate(Screen.Report.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
                composable(Screen.Report.route) {
                    ReportScreen(
                        userId = user?.id ?: "",
                        onRescanClick = { navController.navigate(Screen.Home.route) },
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen(
                        userId = user?.id ?: "",
                        onScanClick = { item ->
                            navController.navigate(Screen.ScanDetail.createRoute(item.id))
                        },
                        onTakeFirstScanClick = { navController.navigate(Screen.Home.route) },
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        user = user,
                        onEditProfileClick = onEditProfileClick,
                        onPrivacyAndDataClick = onPrivacyAndDataClick,
                    )
                }
                composable(Screen.ScanDetail.route) { backStackEntry ->
                    val scanId = backStackEntry.arguments?.getString("scanId") ?: ""
                    ScanDetailScreen(
                        userId = user?.id ?: "",
                        scanId = scanId,
                        onBackClick = { navController.popBackStack() },
                    )
                }
            }
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}
