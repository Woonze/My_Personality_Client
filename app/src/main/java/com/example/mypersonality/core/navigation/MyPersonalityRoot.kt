package com.example.mypersonality.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mypersonality.MainViewModel
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.feature.auth.AuthScreen
import com.example.mypersonality.feature.employer.EmployerVacanciesScreen
import com.example.mypersonality.feature.profile.ProfileScreen
import com.example.mypersonality.feature.search.SearchScreen
import com.example.mypersonality.feature.vacancy.ApplicationsScreen
import com.example.mypersonality.feature.vacancy.VacancyDetailScreen
import com.example.mypersonality.feature.vacancy.VacancyEditorScreen

@Composable
fun MyPersonalityRoot(
    viewModel: MainViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()

    if (session == null) {
        AuthScreen()
        return
    }

    val navController = rememberNavController()
    val userSession = remember(session) { requireNotNull(session) }

    MainShell(
        navController = navController,
        role = userSession.role,
        onThemeChanged = viewModel::toggleTheme
    )
}

@Composable
private fun MainShell(
    navController: NavHostController,
    role: UserRole,
    onThemeChanged: (Boolean) -> Unit
) {
    val destinations = remember(role) {
        if (role == UserRole.SEEKER) {
            listOf(TopLevelRoute.Search, TopLevelRoute.Favorites, TopLevelRoute.Profile)
        } else {
            listOf(TopLevelRoute.MyVacancies, TopLevelRoute.Profile)
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in destinations.map { it.route }) {
                NavigationBar {
                    destinations.forEach { route ->
                        NavigationBarItem(
                            selected = currentRoute == route.route,
                            onClick = {
                                navController.navigate(route.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(route.icon, contentDescription = route.label) },
                            label = { Text(route.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (role == UserRole.SEEKER) {
                TopLevelRoute.Search.route
            } else {
                TopLevelRoute.MyVacancies.route
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelRoute.Search.route) {
                SearchScreen(
                    onVacancyClick = { navController.navigate("vacancy/$it") },
                    favoritesOnly = false
                )
            }
            composable(TopLevelRoute.Favorites.route) {
                SearchScreen(
                    onVacancyClick = { navController.navigate("vacancy/$it") },
                    favoritesOnly = true
                )
            }
            composable(TopLevelRoute.MyVacancies.route) {
                EmployerVacanciesScreen(
                    onCreateVacancy = { navController.navigate("vacancy-editor") },
                    onVacancyClick = { navController.navigate("vacancy/$it") }
                )
            }
            composable(TopLevelRoute.Profile.route) {
                ProfileScreen(
                    onThemeChanged = onThemeChanged,
                    onApplicationsClick = { navController.navigate("applications") }
                )
            }
            composable("vacancy/{vacancyId}") {
                VacancyDetailScreen(
                    onBack = navController::popBackStack,
                    onApplicantsClick = { navController.navigate("vacancy/$it/applications") },
                    onEditClick = { navController.navigate("vacancy-editor/$it") },
                    onDeleted = { navController.popBackStack() }
                )
            }
            composable("vacancy-editor") {
                VacancyEditorScreen(onBack = navController::popBackStack)
            }
            composable("vacancy-editor/{vacancyId}") {
                VacancyEditorScreen(onBack = navController::popBackStack)
            }
            composable("applications") {
                ApplicationsScreen(onBack = navController::popBackStack)
            }
            composable("vacancy/{vacancyId}/applications") {
                ApplicationsScreen(
                    onBack = navController::popBackStack,
                    employerMode = true
                )
            }
        }
    }
}

private enum class TopLevelRoute(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Search("search", "Поиск", Icons.Outlined.Search),
    Favorites("favorites", "Избранное", Icons.Outlined.BookmarkBorder),
    MyVacancies("my_vacancies", "Мои вакансии", Icons.Outlined.WorkOutline),
    Profile("profile", "Профиль", Icons.Outlined.AccountCircle)
}
