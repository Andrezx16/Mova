package com.uilover.project304.ui.saved

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.StringRes
import com.uilover.project304.R
import com.uilover.project304.data.mock.MockData
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.components.LuxeBottomNavBar
import com.uilover.project304.ui.components.LuxeTopBar
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnPrimary
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.OutlineVariant
import com.uilover.project304.ui.theme.Primary
import com.uilover.project304.ui.theme.Surface
import kotlinx.coroutines.launch

enum class SavedSortOption(@StringRes val labelRes: Int) {
    RECENT(R.string.sort_recent),
    PRICE_LOW_HIGH(R.string.sort_price_low_high),
    PRICE_HIGH_LOW(R.string.sort_price_high_low)
}

@Composable
fun SavedPropertiesScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToScheduleTour: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var savedList by remember { mutableStateOf(MockData.savedProperties) }
    var selectedSort by remember { mutableStateOf(SavedSortOption.RECENT) }

    val sortedList = remember(savedList, selectedSort) {
        when (selectedSort) {
            SavedSortOption.RECENT -> savedList
            SavedSortOption.PRICE_LOW_HIGH -> savedList.sortedBy { it.price }
            SavedSortOption.PRICE_HIGH_LOW -> savedList.sortedByDescending { it.price }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = {
            LuxeTopBar(
                userProfile = MockData.currentUser,
                onMenuClick = {
                    Toast.makeText(context, "Menu opened", Toast.LENGTH_SHORT).show()
                },
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            LuxeBottomNavBar(
                selectedTab = HomeNavTab.SAVED,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeNavTab.HOME -> onNavigateToHome()
                        HomeNavTab.SEARCH -> onNavigateToSearch()
                        HomeNavTab.SAVED -> { /* Already on Saved */ }
                        HomeNavTab.PROFILE -> onNavigateToProfile()
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
            // Page Title Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.saved_properties_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.saved_properties_count, sortedList.size),
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Sort Filter Chips Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SavedSortOption.values()) { option ->
                        val isSelected = option == selectedSort

                        val bg by animateColorAsState(
                            targetValue = if (isSelected) Primary else CardBackground,
                            label = "sortBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) OnPrimary else OnSurface,
                            label = "sortText"
                        )

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedSort = option
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(option.labelRes),
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Saved Property Cards
            if (sortedList.isNotEmpty()) {
                items(
                    items = sortedList,
                    key = { it.id }
                ) { property ->
                    SavedPropertyCard(
                        property = property,
                        onRemoveFavorite = {
                            savedList = savedList.filter { it.id != property.id }
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Removed ${property.title} from Saved")
                            }
                        },
                        onScheduleTourClick = {
                            onNavigateToScheduleTour(property.id)
                        },
                        onClick = {
                            onNavigateToPropertyDetail(property.id)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_saved_properties),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.no_saved_hint),
                            fontSize = 14.sp,
                            color = OnSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
