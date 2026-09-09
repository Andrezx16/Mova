package com.uilover.project304.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uilover.project304.ui.details.PropertyDetailScreen
import com.uilover.project304.ui.home.HomeScreen
import com.uilover.project304.ui.tour.ScheduleTourScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        }
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {


            HomeScreen(
                onPropertyClick = { property ->
                    navController.navigate(Screen.PropertyDetail.createRoute(property.id))
                },
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Search Properties Screen
        composable(route = Screen.Search.route) {
            com.uilover.project304.ui.search.SearchPropertiesScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                }
            )
        }

        // Saved Properties Screen
        composable(route = Screen.Saved.route) {
            com.uilover.project304.ui.saved.SavedPropertiesScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                },
                onNavigateToScheduleTour = { propertyId ->
                    navController.navigate(Screen.ScheduleTour.createRoute(propertyId))
                }
            )
        }

        // User Profile Screen
        composable(route = Screen.Profile.route) {
            com.uilover.project304.ui.profile.UserProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route)
                },
                onNavigateToScheduleTour = { propertyId ->
                    navController.navigate(Screen.ScheduleTour.createRoute(propertyId))
                }
            )
        }

        // Property Detail Screen
        composable(
            route = Screen.PropertyDetail.route,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                    defaultValue = "feat-1"
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: "feat-1"
            PropertyDetailScreen(
                propertyId = propertyId,
                onBackClick = { navController.popBackStack() },
                onScheduleTourClick = { id ->
                    navController.navigate(Screen.ScheduleTour.createRoute(id))
                }
            )
        }

        // Schedule Tour Screen
        composable(
            route = Screen.ScheduleTour.route,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                    defaultValue = "feat-1"
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: "feat-1"
            ScheduleTourScreen(
                propertyId = propertyId,
                onBackClick = { navController.popBackStack() },
                onConfirmBooking = { navController.popBackStack() }
            )
        }
    }
}


