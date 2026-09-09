package com.uilover.project304.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.uilover.project304.R

enum class HomeNavTab(
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    SEARCH(R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search),
    SAVED(R.string.nav_saved, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    PROFILE(R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}
