package com.uilover.project304.ui.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Villa
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import com.uilover.project304.R
import com.uilover.project304.data.mock.MockData
import com.uilover.project304.data.model.HomeNavTab
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.ui.components.LuxeBottomNavBar
import com.uilover.project304.ui.components.LuxeTopBar
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnPrimary
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.OutlineVariant
import com.uilover.project304.ui.theme.Primary
import com.uilover.project304.ui.theme.Surface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchPropertiesScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPropertyDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedPropertyType by remember { mutableStateOf("All") }
    var priceRange by remember { mutableStateOf(500000f..5000000f) }
    var selectedBedrooms by remember { mutableStateOf("Any") }
    var hasPool by remember { mutableStateOf(false) }
    var hasGym by remember { mutableStateOf(false) }
    var hasParking by remember { mutableStateOf(false) }
    var hasGarden by remember { mutableStateOf(false) }

    // Search Execution State: initially false so results are only shown after filter/search
    var hasAppliedFilters by remember { mutableStateOf(false) }
    var filteredResults by remember { mutableStateOf<List<Property>>(emptyList()) }

    var selectedSortOption by remember { mutableStateOf("Recommended") }
    var sortDropdownExpanded by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var favoriteIds by remember { mutableStateOf(setOf<String>()) }

    val sortOptions = listOf("Recommended", "Price: Low to High", "Price: High to Low", "Newest")
    val itemsPerPage = 4

    // Distinct property pool from mock data
    val allCatalog = remember {
        MockData.allProperties.distinctBy { it.id }
    }

    // Function to filter and sort properties accurately
    fun applyFiltering(
        query: String = searchQuery,
        type: String = selectedPropertyType,
        range: ClosedFloatingPointRange<Float> = priceRange,
        bedrooms: String = selectedBedrooms,
        pool: Boolean = hasPool,
        gym: Boolean = hasGym,
        parking: Boolean = hasParking,
        garden: Boolean = hasGarden,
        sort: String = selectedSortOption
    ) {
        val trimmedQuery = query.trim().lowercase()

        val matching = allCatalog.filter { property ->
            // 1. Text Search Matching
            val matchesQuery = if (trimmedQuery.isBlank()) {
                true
            } else {
                property.title.lowercase().contains(trimmedQuery) ||
                        property.address.lowercase().contains(trimmedQuery) ||
                        property.description.lowercase().contains(trimmedQuery) ||
                        property.category.name.lowercase().contains(trimmedQuery)
            }

            // 2. Property Type Matching
            val matchesType = when (type) {
                "All" -> true
                "House" -> property.category == PropertyCategory.HOUSE || property.category == PropertyCategory.TOWNHOUSE
                "Villa" -> property.category == PropertyCategory.VILLA
                "Apartment" -> property.category == PropertyCategory.APARTMENT || property.category == PropertyCategory.PENTHOUSE
                "Studio" -> property.beds == 0 || property.title.contains("Studio", ignoreCase = true) || property.description.contains("Studio", ignoreCase = true)
                else -> true
            }

            // 3. Price Range Matching
            val matchesPrice = property.price >= range.start && property.price <= range.endInclusive

            // 4. Bedroom Count Matching
            val matchesBedrooms = when (bedrooms) {
                "Any" -> true
                "1" -> property.beds == 1
                "2" -> property.beds == 2
                "3" -> property.beds == 3
                "4+" -> property.beds >= 4
                else -> true
            }

            // 5. Amenities Matching
            val matchesPool = if (!pool) true else {
                property.amenities.any { it.contains("pool", ignoreCase = true) } ||
                        property.description.contains("pool", ignoreCase = true)
            }

            val matchesGym = if (!gym) true else {
                property.amenities.any {
                    it.contains("gym", ignoreCase = true) ||
                            it.contains("fitness", ignoreCase = true) ||
                            it.contains("spa", ignoreCase = true)
                } || property.description.contains("gym", ignoreCase = true) ||
                        property.description.contains("fitness", ignoreCase = true)
            }

            val matchesParking = if (!parking) true else {
                property.amenities.any {
                    it.contains("garage", ignoreCase = true) ||
                            it.contains("parking", ignoreCase = true) ||
                            it.contains("valet", ignoreCase = true)
                } || property.description.contains("garage", ignoreCase = true) ||
                        property.description.contains("parking", ignoreCase = true)
            }

            val matchesGarden = if (!garden) true else {
                property.amenities.any {
                    it.contains("garden", ignoreCase = true) ||
                            it.contains("yard", ignoreCase = true) ||
                            it.contains("courtyard", ignoreCase = true) ||
                            it.contains("terrace", ignoreCase = true)
                } || property.description.contains("garden", ignoreCase = true) ||
                        property.description.contains("yard", ignoreCase = true)
            }

            matchesQuery && matchesType && matchesPrice && matchesBedrooms && matchesPool && matchesGym && matchesParking && matchesGarden
        }

        // Apply Sorting
        val sorted = when (sort) {
            "Price: Low to High" -> matching.sortedBy { it.price }
            "Price: High to Low" -> matching.sortedByDescending { it.price }
            "Newest" -> matching.sortedByDescending { it.badge != null }
            else -> matching.sortedByDescending { it.rating }
        }

        filteredResults = sorted
        hasAppliedFilters = true
        currentPage = 1
    }

    // Function to reset all filters
    fun resetAllFilters() {
        searchQuery = ""
        selectedPropertyType = "All"
        priceRange = 500000f..5000000f
        selectedBedrooms = "Any"
        hasPool = false
        hasGym = false
        hasParking = false
        hasGarden = false
        selectedSortOption = "Recommended"
        currentPage = 1
        hasAppliedFilters = false
        filteredResults = emptyList()
        Toast.makeText(context, context.getString(R.string.filters_reset_toast), Toast.LENGTH_SHORT).show()
    }

    // Calculate pagination slice
    val totalPages = if (filteredResults.isEmpty()) 1 else ((filteredResults.size + itemsPerPage - 1) / itemsPerPage)
    val clampedPage = currentPage.coerceIn(1, totalPages)
    val pageItems = if (filteredResults.isEmpty()) {
        emptyList()
    } else {
        filteredResults.drop((clampedPage - 1) * itemsPerPage).take(itemsPerPage)
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
                selectedTab = HomeNavTab.SEARCH,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeNavTab.HOME -> onNavigateToHome()
                        HomeNavTab.SEARCH -> { /* Already on Search */ }
                        HomeNavTab.SAVED -> onNavigateToSaved()
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
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // Search Input Field
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(CardBackground)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_input_placeholder),
                                    fontSize = 14.sp,
                                    color = OnSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = OnSurface,
                                    fontSize = 14.sp
                                ),
                                cursorBrush = SolidColor(Primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                        applyFiltering()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    if (hasAppliedFilters) {
                                        applyFiltering(query = "")
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Expandable Filter Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateContentSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(OutlineVariant.copy(alpha = 0.6f))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Header (Filters + Reset)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.filter_properties),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { resetAllFilters() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.RestartAlt,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.reset_filters),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // PROPERTY TYPE
                        Text(
                            text = stringResource(R.string.property_type_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val types = listOf(
                                "All" to R.string.category_all,
                                "House" to R.string.category_house,
                                "Villa" to R.string.category_villa,
                                "Apartment" to R.string.category_apartment,
                                "Studio" to R.string.category_studio
                            )
                            items(types) { (typeKey, labelRes) ->
                                val isSelected = typeKey == selectedPropertyType
                                FilterPill(
                                    text = stringResource(labelRes),
                                    isSelected = isSelected,
                                    onClick = { selectedPropertyType = typeKey }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // PRICE RANGE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.price_range_label),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "$${"%,d".format(priceRange.start.toLong())} - $${"%,d".format(priceRange.endInclusive.toLong())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        RangeSlider(
                            value = priceRange,
                            onValueChange = { priceRange = it },
                            valueRange = 200000f..10000000f,
                            steps = 49,
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = Primary,
                                inactiveTrackColor = OutlineVariant.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Min / Max Value Boxes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.min_price), fontSize = 11.sp, color = OnSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF6F7FA))
                                        .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "$${"%,d".format(priceRange.start.toLong())}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurface
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.max_price), fontSize = 11.sp, color = OnSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF6F7FA))
                                        .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "$${"%,d".format(priceRange.endInclusive.toLong())}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // BEDROOMS
                        Text(
                            text = stringResource(R.string.bedrooms_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Any", "1", "2", "3", "4+").forEach { count ->
                                val isSelected = count == selectedBedrooms
                                val displayText = if (count == "Any") stringResource(R.string.bedroom_any) else count
                                BedroomSelectorBox(
                                    text = displayText,
                                    isSelected = isSelected,
                                    onClick = { selectedBedrooms = count },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // AMENITIES
                        Text(
                            text = stringResource(R.string.amenities_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AmenityCheckboxItem(
                                    title = stringResource(R.string.pool),
                                    isChecked = hasPool,
                                    onCheckedChange = { hasPool = it },
                                    modifier = Modifier.weight(1f)
                                )
                                AmenityCheckboxItem(
                                    title = stringResource(R.string.gym_spa),
                                    isChecked = hasGym,
                                    onCheckedChange = { hasGym = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AmenityCheckboxItem(
                                    title = stringResource(R.string.parking_garage),
                                    isChecked = hasParking,
                                    onCheckedChange = { hasParking = it },
                                    modifier = Modifier.weight(1f)
                                )
                                AmenityCheckboxItem(
                                    title = stringResource(R.string.garden_yard),
                                    isChecked = hasGarden,
                                    onCheckedChange = { hasGarden = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Apply Filters Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                applyFiltering()
                                val count = filteredResults.size
                                val msg = if (count == 0) context.getString(R.string.no_matching_found) else context.getString(R.string.properties_found_plural, count)
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterAlt,
                                    contentDescription = null,
                                    tint = OnPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.apply_filters),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // State 1: INITIAL STATE (Before applying filters)
            if (!hasAppliedFilters) {
                item {
                    InitialSearchPrompt(
                        onQuickFilter = { type, bedrooms, pool ->
                            selectedPropertyType = type
                            selectedBedrooms = bedrooms
                            hasPool = pool
                            applyFiltering(
                                type = type,
                                bedrooms = bedrooms,
                                pool = pool
                            )
                        }
                    )
                }
            } else if (filteredResults.isEmpty()) {
                // State 2: EMPTY STATE (Filters applied, 0 results)
                item {
                    EmptyResultsView(onResetFilters = { resetAllFilters() })
                }
            } else {
                // State 3: RESULTS FOUND (Filters applied, >0 results)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (filteredResults.size == 1) stringResource(R.string.one_property_found) else stringResource(R.string.properties_found_plural, filteredResults.size),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )

                        // Sort Dropdown Button
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CardBackground)
                                    .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .clickable { sortDropdownExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val displaySort = when (selectedSortOption) {
                                    "Price: Low to High" -> stringResource(R.string.sort_price_low_high)
                                    "Price: High to Low" -> stringResource(R.string.sort_price_high_low)
                                    "Newest" -> stringResource(R.string.sort_newest)
                                    else -> stringResource(R.string.sort_recommended)
                                }
                                Text(
                                    text = displaySort,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OnSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Sort",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = sortDropdownExpanded,
                                onDismissRequest = { sortDropdownExpanded = false }
                            ) {
                                sortOptions.forEach { option ->
                                    val labelRes = when (option) {
                                        "Price: Low to High" -> R.string.sort_price_low_high
                                        "Price: High to Low" -> R.string.sort_price_high_low
                                        "Newest" -> R.string.sort_newest
                                        else -> R.string.sort_recommended
                                    }
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(labelRes), fontSize = 13.sp) },
                                        onClick = {
                                            selectedSortOption = option
                                            sortDropdownExpanded = false
                                            filteredResults = when (option) {
                                                "Price: Low to High" -> filteredResults.sortedBy { it.price }
                                                "Price: High to Low" -> filteredResults.sortedByDescending { it.price }
                                                "Newest" -> filteredResults.sortedByDescending { it.badge != null }
                                                else -> filteredResults.sortedByDescending { it.rating }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Search Results List (Paginated)
                items(
                    items = pageItems,
                    key = { it.id }
                ) { property ->
                    SearchResultCard(
                        property = property,
                        isFavorite = favoriteIds.contains(property.id),
                        onFavoriteToggle = { id ->
                            favoriteIds = if (favoriteIds.contains(id)) favoriteIds - id else favoriteIds + id
                        },
                        onClick = { onNavigateToPropertyDetail(property.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }

                // Dynamic Pagination Controls (if more than 1 page)
                if (totalPages > 1) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Prev Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { if (currentPage > 1) currentPage-- },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Page",
                                    tint = if (currentPage > 1) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Dynamic Page Numbers
                            (1..totalPages).forEach { page ->
                                val isSelected = page == clampedPage

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Primary else CardBackground)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { currentPage = page },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = page.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) OnPrimary else OnSurface
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Next Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { if (currentPage < totalPages) currentPage++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Page",
                                    tint = if (currentPage < totalPages) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

/**
 * Initial State View: Displayed before user applies filters.
 * Offers guidance and quick search filter presets.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InitialSearchPrompt(
    onQuickFilter: (type: String, bedrooms: String, pool: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(OutlineVariant.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.initial_search_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.initial_search_subtitle),
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.quick_filters),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                QuickPresetChip(
                    icon = Icons.Outlined.Villa,
                    text = stringResource(R.string.villas_with_pool),
                    onClick = { onQuickFilter("Villa", "Any", true) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickPresetChip(
                    icon = Icons.Outlined.Apartment,
                    text = stringResource(R.string.modern_apartments),
                    onClick = { onQuickFilter("Apartment", "Any", false) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickPresetChip(
                    icon = Icons.Outlined.Home,
                    text = stringResource(R.string.luxury_houses_3bed),
                    onClick = { onQuickFilter("House", "3", false) }
                )
            }
        }
    }
}

@Composable
private fun QuickPresetChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F4F9))
            .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Primary
        )
    }
}

/**
 * Empty Results View: Displayed when filters are applied but 0 properties match.
 */
@Composable
private fun EmptyResultsView(onResetFilters: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(OutlineVariant.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.no_results_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.no_results_subtitle),
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = onResetFilters,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Reset All Filters", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Primary else Color(0xFFF6F7FA),
        label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurface,
        label = "pillText"
    )

    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(19.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun BedroomSelectorBox(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Primary else Color(0xFFF6F7FA),
        label = "bedBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurface,
        label = "bedText"
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun AmenityCheckboxItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) Primary else Color(0xFFF6F7FA))
                .border(
                    width = 1.5.dp,
                    color = if (isChecked) Primary else OutlineVariant,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isChecked) OnSurface else OnSurfaceVariant
        )
    }
}


