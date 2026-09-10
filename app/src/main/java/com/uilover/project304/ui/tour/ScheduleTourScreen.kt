package com.uilover.project304.ui.tour

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.R
import com.uilover.project304.ui.components.PropertyImage
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnPrimary
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.OutlineVariant
import com.uilover.project304.ui.theme.Primary
import com.uilover.project304.ui.theme.Surface

data class TourDate(
    val month: String,
    val day: String,
    val dayOfWeek: String
)

enum class TourType(@StringRes val labelRes: Int) {
    IN_PERSON(R.string.tour_in_person),
    VIDEO_CALL(R.string.tour_video_call)
}

@Composable
fun ScheduleTourScreen(
    propertyId: String,
    onBackClick: () -> Unit,
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleTourViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(propertyId) { viewModel.loadProperty(propertyId) }

    val propertyState by viewModel.property.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isBookingSuccessful by viewModel.isBookingSuccessful.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(isBookingSuccessful) {
        if (isBookingSuccessful) {
            onConfirmBooking()
            viewModel.resetBookingState()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetBookingState()
        }
    }

    if (propertyState == null) {
        Box(
            modifier = modifier.fillMaxSize().background(Surface),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    val property = propertyState!!

    val availableDates = remember {
        listOf(
            TourDate("OCT", "24", "Tue"),
            TourDate("OCT", "25", "Wed"),
            TourDate("OCT", "26", "Thu"),
            TourDate("OCT", "27", "Fri"),
            TourDate("OCT", "28", "Sat"),
            TourDate("OCT", "29", "Sun")
        )
    }

    val availableTimes = remember {
        listOf("9:00 AM", "10:30 AM", "1:00 PM", "3:30 PM", "5:00 PM")
    }

    var selectedDate by remember { mutableStateOf(availableDates[0]) }
    var selectedTime by remember { mutableStateOf(availableTimes[0]) }
    var selectedTourType by remember { mutableStateOf(TourType.IN_PERSON) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.schedule_a_tour),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp),
                color = CardBackground
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.bookTour(
                                propertyId = property.id,
                                propertyTitle = property.title,
                                date = "${selectedDate.day} ${selectedDate.month}",
                                time = selectedTime,
                                tourType = selectedTourType.name
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = OnPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.confirm_booking),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Property Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineVariant.copy(alpha = 0.4f)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PropertyImage(
                        property = property,
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = property.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = property.address,
                                fontSize = 13.sp,
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Select Date Section
            Text(
                text = stringResource(R.string.select_date),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(availableDates) { item ->
                    val isSelected = item == selectedDate

                    val bg by animateColorAsState(
                        targetValue = if (isSelected) Primary else CardBackground,
                        label = "dateBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) OnPrimary else OnSurface,
                        label = "dateText"
                    )
                    val subTextColor by animateColorAsState(
                        targetValue = if (isSelected) OnPrimary.copy(alpha = 0.85f) else OnSurfaceVariant,
                        label = "dateSubText"
                    )

                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(98.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedDate = item
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.month,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = subTextColor,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.day,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.dayOfWeek,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = subTextColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Select Time Section
            Text(
                text = stringResource(R.string.select_time),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1 of Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableTimes.take(3).forEach { time ->
                    val isSelected = time == selectedTime
                    TimeChip(
                        time = time,
                        isSelected = isSelected,
                        onClick = { selectedTime = time },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2 of Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableTimes.drop(3).forEach { time ->
                    val isSelected = time == selectedTime
                    TimeChip(
                        time = time,
                        isSelected = isSelected,
                        onClick = { selectedTime = time },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Tour Type Section
            Text(
                text = stringResource(R.string.tour_type),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // In-Person Card
                val isInPerson = selectedTourType == TourType.IN_PERSON
                TourTypeCard(
                    title = stringResource(TourType.IN_PERSON.labelRes),
                    icon = Icons.Filled.Home,
                    isSelected = isInPerson,
                    onClick = { selectedTourType = TourType.IN_PERSON },
                    modifier = Modifier.weight(1f)
                )

                // Video Call Card
                val isVideoCall = selectedTourType == TourType.VIDEO_CALL
                TourTypeCard(
                    title = stringResource(TourType.VIDEO_CALL.labelRes),
                    icon = Icons.Filled.Videocam,
                    isSelected = isVideoCall,
                    onClick = { selectedTourType = TourType.VIDEO_CALL },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Primary else CardBackground,
        label = "timeChipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurface,
        label = "timeChipText"
    )

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
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
            text = time,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun TourTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Primary else CardBackground,
        label = "tourTypeBg"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurface,
        label = "tourTypeTint"
    )

    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        }
    }
}
