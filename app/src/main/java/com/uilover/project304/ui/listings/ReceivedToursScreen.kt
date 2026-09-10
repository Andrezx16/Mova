package com.uilover.project304.ui.listings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ReceivedToursScreen(onBackClick: () -> Unit, viewModel: ListingsViewModel = hiltViewModel()) {
    val tours by viewModel.receivedTours.collectAsState()
    Scaffold(containerColor = Surface, topBar = { Row(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Primary) }; Text(stringResource(R.string.received_tours), color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold) } }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (tours.isEmpty()) item { Text(stringResource(R.string.no_received_tours), color = OnSurfaceVariant) }
            items(tours, key = { it.id }) { tour ->
                Card(colors = CardDefaults.cardColors(containerColor = CardBackground), shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(16.dp)) {
                    Text(tour.propertyTitle, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("${tour.date} - ${tour.time}", color = OnSurfaceVariant)
                    Text(tour.status, color = Primary)
                    if (tour.status == TourBooking.STATUS_PENDING) Row { Button(onClick = { viewModel.respond(tour.id, true) }) { Text(stringResource(R.string.accept_tour)) }; Button(onClick = { viewModel.respond(tour.id, false) }) { Text(stringResource(R.string.reject_tour)) } }
                } }
            }
        }
    }
}
