package com.uilover.project304.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object PropertyDetail : Screen("property_detail/{propertyId}") {
        fun createRoute(propertyId: String) = "property_detail/$propertyId"
    }
    object ScheduleTour : Screen("schedule_tour/{propertyId}") {
        fun createRoute(propertyId: String) = "schedule_tour/$propertyId"
    }
    object Tours : Screen("tours/{view}") {
        fun createRoute(view: String) = "tours/$view"
    }
    object Listings : Screen("listings")
    object ListingForm : Screen("listing_form/{propertyId}") {
        fun createRoute(propertyId: String = "new") = "listing_form/$propertyId"
    }
    object ReceivedTours : Screen("received_tours")
    object Saved : Screen("saved")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object About : Screen("about")
}

