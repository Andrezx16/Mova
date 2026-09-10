package com.uilover.project304.ui.tour

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.R
import com.uilover.project304.data.model.TourBooking
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.Primary
import com.uilover.project304.ui.theme.Surface

@Composable
fun ToursScreen(
    showHistory: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ToursViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tours by viewModel.tours.collectAsState()
    val updatingTourId by viewModel.updatingTourId.collectAsState()
    val isClearingHistory by viewModel.isClearingHistory.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val visibleTours = tours.filter {
        if (showHistory) it.status in setOf(TourBooking.STATUS_CANCELLED, TourBooking.STATUS_COMPLETED, TourBooking.STATUS_REJECTED)
        else it.status in setOf(TourBooking.STATUS_PENDING, TourBooking.STATUS_CONFIRMED)
    }
    var showClearHistoryConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Primary
                    )
                }
                Text(
                    text = stringResource(
                        if (showHistory) R.string.tour_history_title else R.string.scheduled_tours_title
                    ),
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                if (showHistory && visibleTours.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearHistoryConfirmation = true },
                        enabled = !isClearingHistory
                    ) {
                        Text(stringResource(R.string.clear_tour_history))
                    }
                }
            }
        }
    ) { paddingValues ->
        if (visibleTours.isEmpty()) {
            EmptyTours(
                showHistory = showHistory,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleTours, key = { it.id }) { tour ->
                    TourCard(
                        tour = tour,
                        isUpdating = updatingTourId == tour.id,
                        onComplete = { viewModel.completeTour(tour.id) },
                        onCancel = { viewModel.cancelTour(tour.id) },
                        onDelete = if (showHistory) { { viewModel.deleteTour(tour.id) } } else null
                    )
                }
            }
        }
    }
    if (showClearHistoryConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirmation = false },
            title = { Text(stringResource(R.string.clear_tour_history)) },
            text = { Text(stringResource(R.string.clear_tour_history_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    showClearHistoryConfirmation = false
                    viewModel.clearHistory()
                }) { Text(stringResource(R.string.delete_listing)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirmation = false }) {
                    Text(stringResource(R.string.language_cancel))
                }
            }
        )
    }
}

@Composable
private fun EmptyTours(showHistory: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (showHistory) Icons.Outlined.Schedule else Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(if (showHistory) R.string.no_tour_history else R.string.no_scheduled_tours),
                color = OnSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (!showHistory) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.no_scheduled_tours_hint),
                    color = OnSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TourCard(
    tour: TourBooking,
    isUpdating: Boolean,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val statusText = when (tour.status) {
        TourBooking.STATUS_PENDING -> stringResource(R.string.tour_status_pending)
        TourBooking.STATUS_CONFIRMED -> stringResource(R.string.tour_status_confirmed)
        TourBooking.STATUS_CANCELLED -> stringResource(R.string.tour_status_cancelled)
        TourBooking.STATUS_COMPLETED -> stringResource(R.string.tour_status_completed)
        TourBooking.STATUS_REJECTED -> stringResource(R.string.tour_status_rejected)
        else -> ""
    }
    val tourTypeText = if (tour.tourType == "VIDEO_CALL") {
        stringResource(R.string.tour_type_video_call)
    } else {
        stringResource(R.string.tour_type_in_person)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(tour.propertyTitle, color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFDCE4F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.CalendarToday, null, tint = Primary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.tour_date_time, tour.date, tour.time),
                        color = OnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(tourTypeText, color = OnSurfaceVariant, fontSize = 13.sp)
                }
                if (statusText.isNotEmpty()) {
                    Text(statusText, color = OnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (tour.status in setOf(TourBooking.STATUS_PENDING, TourBooking.STATUS_CONFIRMED)) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (tour.status == TourBooking.STATUS_CONFIRMED) {
                        Button(
                            onClick = onComplete,
                            enabled = !isUpdating,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text(stringResource(R.string.complete_tour))
                            }
                        }
                    }
                    Button(
                        onClick = onCancel,
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFE4E6),
                            contentColor = Color(0xFFB42318)
                        )
                    ) {
                        Text(stringResource(R.string.cancel_tour))
                    }
                }
            } else if (onDelete != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDelete,
                    enabled = !isUpdating,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.delete_tour))
                }
            }
        }
    }
}
