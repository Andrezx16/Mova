package com.uilover.project304.ui.listings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.R
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.components.PropertyImage
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.Primary
import com.uilover.project304.ui.theme.Surface

@Composable
fun ListingsScreen(onBackClick: () -> Unit, onAddListing: () -> Unit, onEditListing: (String) -> Unit, onViewRequests: () -> Unit, viewModel: ListingsViewModel = hiltViewModel()) {
    val properties by viewModel.properties.collectAsState()
    val requests by viewModel.receivedTours.collectAsState()
    Scaffold(containerColor = Surface, topBar = {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Primary) }
            Text(stringResource(R.string.my_listings), color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = onAddListing) { Icon(Icons.Default.Add, stringResource(R.string.add_listing), tint = Primary) }
        }
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Button(onClick = onViewRequests, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text(stringResource(R.string.received_requests, requests.count { it.status == "PENDING" }))
                }
            }
            if (properties.isEmpty()) item { Text(stringResource(R.string.no_listings), color = OnSurfaceVariant) }
            items(properties, key = { it.id }) { property ->
                ListingCard(property, onEdit = { onEditListing(property.id) }, onToggle = { viewModel.setActive(property) }, onDelete = { viewModel.delete(property.id) })
            }
        }
    }
}

@Composable private fun ListingCard(property: Property, onEdit: () -> Unit, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                PropertyImage(
                    property = property,
                    contentDescription = property.title,
                    modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(property.title, color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(property.address, color = OnSurfaceVariant, fontSize = 13.sp)
                    Text(if (property.isActive) stringResource(R.string.listing_active) else stringResource(R.string.listing_paused), color = if (property.isActive) Primary else OnSurfaceVariant, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onEdit) { Text(stringResource(R.string.edit_listing)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onToggle) { Text(stringResource(if (property.isActive) R.string.pause_listing else R.string.activate_listing)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFB42318))) { Text(stringResource(R.string.delete_listing)) }
            }
        }
    }
}
