package com.uilover.project304.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PropertyDetail : Screen("property_detail/{propertyId}") {



        fun createRoute(propertyId: String) = "property_detail/$propertyId"
    }
    object ScheduleTour : Screen("schedule_tour/{propertyId}") {
        fun createRoute(propertyId: String) = "schedule_tour/$propertyId"
    }
    object Saved : Screen("saved")
    object Search : Screen("search")
    object Profile : Screen("profile")
}



