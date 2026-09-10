package com.uilover.project304.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.components.CategoryChipsRow
import com.uilover.project304.ui.components.FeaturedPropertyCard
import com.uilover.project304.ui.components.FilterBottomSheet
import com.uilover.project304.ui.components.LuxeBottomNavBar
import com.uilover.project304.ui.components.LuxeSearchBar
import com.uilover.project304.ui.components.LuxeTopBar
import com.uilover.project304.ui.components.NatureBackdrop
import com.uilover.project304.ui.components.RecommendedPropertyCard
import com.uilover.project304.ui.theme.AntonFamily
import com.uilover.project304.ui.theme.Cream
import com.uilover.project304.ui.theme.DeepBrown
import com.uilover.project304.ui.theme.NatureImagery
import com.uilover.project304.ui.theme.QuicksandFamily
import com.uilover.project304.ui.theme.SageGreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onPropertyClick: (Property) -> Unit = {},
    onNavigateToSaved: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBrown)
    ) {
        NatureBackdrop(
            imageUrl = NatureImagery.HOME_HERO,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                LuxeTopBar(userProfile = uiState.userProfile)
            },
            bottomBar = {
                LuxeBottomNavBar(
                    selectedTab = uiState.selectedNavTab,
                    onTabSelected = { tab ->
                        viewModel.onNavTabSelected(tab)
                        if (tab == HomeNavTab.SAVED) {
                            onNavigateToSaved()
                        } else if (tab == HomeNavTab.SEARCH) {
                            onNavigateToSearch()
                        } else if (tab == HomeNavTab.PROFILE) {
                            onNavigateToProfile()
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Hero heading
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(com.uilover.project304.R.string.home_hero_title),
                        fontFamily = AntonFamily,
                        fontSize = 40.sp,
                        lineHeight = 42.sp,
                        color = Cream,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(com.uilover.project304.R.string.home_hero_subtitle),
                        fontFamily = QuicksandFamily,
                        fontSize = 14.sp,
                        color = Cream.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Search and Filter Bar
                item {
                    LuxeSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onFilterClick = { viewModel.onToggleFilterDialog() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Category Chips Row
                item {
                    CategoryChipsRow(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { viewModel.onCategorySelected(it) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Featured Properties Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(com.uilover.project304.R.string.featured_properties),
                            fontFamily = QuicksandFamily,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Cream
                        )
                        Text(
                            text = stringResource(com.uilover.project304.R.string.view_all),
                            fontFamily = QuicksandFamily,
                            fontSize = 13.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = SageGreen,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, context.getString(com.uilover.project304.R.string.featured_properties), Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Featured Properties Cards (Stacked list matching screenshot)
                val featuredList = uiState.filteredFeaturedProperties
                if (featuredList.isNotEmpty()) {
                    items(
                        items = featuredList,
                        key = { "feat_${it.id}" }
                    ) { property ->
                        FeaturedPropertyCard(
                            property = property,
                            isFavorite = uiState.favoritePropertyIds.contains(property.id),
                            onFavoriteToggle = { viewModel.onFavoriteToggle(property.id) },
                            onClick = { onPropertyClick(property) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(com.uilover.project304.R.string.no_featured_match),
                            color = Cream.copy(alpha = 0.7f),
                            fontFamily = QuicksandFamily,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }

                // Recommended for You Section Header
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(com.uilover.project304.R.string.recommended_for_you),
                        fontFamily = QuicksandFamily,
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Cream,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Recommended Properties List
                val recommendedList = uiState.filteredRecommendedProperties
                if (recommendedList.isNotEmpty()) {
                    items(
                        items = recommendedList,
                        key = { "rec_${it.id}" }
                    ) { property ->
                        RecommendedPropertyCard(
                            property = property,
                            isFavorite = uiState.favoritePropertyIds.contains(property.id),
                            onFavoriteToggle = { viewModel.onFavoriteToggle(property.id) },
                            onClick = { onPropertyClick(property) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(com.uilover.project304.R.string.no_properties_found),
                            color = Cream.copy(alpha = 0.7f),
                            fontFamily = QuicksandFamily,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet Dialog
    if (uiState.isFilterDialogOpen) {
        FilterBottomSheet(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.onCategorySelected(it) },
            onDismiss = { viewModel.onToggleFilterDialog() },
            onApplyFilters = {
                viewModel.onToggleFilterDialog()
                Toast.makeText(context, context.getString(com.uilover.project304.R.string.filters_applied_toast), Toast.LENGTH_SHORT).show()
            }
        )
    }
}
