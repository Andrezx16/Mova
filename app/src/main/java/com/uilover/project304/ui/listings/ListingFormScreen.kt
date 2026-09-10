package com.uilover.project304.ui.listings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.uilover.project304.R
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.ui.theme.Cream as CardBackground
import com.uilover.project304.ui.theme.DeepBrown as OnSurface
import com.uilover.project304.ui.theme.WarmTaupe as OnSurfaceVariant
import com.uilover.project304.ui.theme.SageGreen as Primary
import com.uilover.project304.ui.theme.Cream as Surface

@Composable
fun ListingFormScreen(
    propertyId: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ListingsViewModel = hiltViewModel()
) {
    val properties by viewModel.properties.collectAsState()
    val existing = properties.firstOrNull { it.id == propertyId }
    val context = LocalContext.current
    var title by remember(existing) { mutableStateOf(existing?.title.orEmpty()) }
    var price by remember(existing) { mutableStateOf(existing?.price?.toLong()?.toString().orEmpty()) }
    var address by remember(existing) { mutableStateOf(existing?.address.orEmpty()) }
    var beds by remember(existing) { mutableStateOf(existing?.beds?.toString().orEmpty()) }
    var baths by remember(existing) { mutableStateOf(existing?.baths?.toString().orEmpty()) }
    var sqft by remember(existing) { mutableStateOf(existing?.sqft?.toString().orEmpty()) }
    var description by remember(existing) { mutableStateOf(existing?.description.orEmpty()) }
    var amenities by remember(existing) { mutableStateOf(existing?.amenities?.joinToString(", ").orEmpty()) }
    var category by remember(existing) { mutableStateOf(existing?.category ?: PropertyCategory.HOUSE) }
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(containerColor = Surface, topBar = {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Primary) }
            Text(
                stringResource(if (propertyId == "new") R.string.add_listing else R.string.edit_listing),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.listing_required_hint), color = OnSurfaceVariant, fontSize = 13.sp)
            ListingTextField(title, { title = it }, R.string.listing_title, required = true)
            ListingTextField(price, { price = it }, R.string.listing_price, required = true, keyboardType = KeyboardType.Decimal, helper = R.string.listing_price_helper)
            ListingTextField(address, { address = it }, R.string.listing_address, required = true)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ListingTextField(beds, { beds = it }, R.string.listing_beds, required = true, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                ListingTextField(baths, { baths = it }, R.string.listing_baths, required = true, keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
            }
            ListingTextField(sqft, { sqft = it }, R.string.listing_sqft, required = true, keyboardType = KeyboardType.Number, helper = R.string.listing_sqft_helper)

            Text(stringResource(R.string.listing_category_required), color = OnSurface, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PropertyCategory.entries.filter { it != PropertyCategory.ALL }.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(stringResource(item.labelRes)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Surface)
                    )
                }
            }

            Text(stringResource(R.string.listing_photo_required), color = OnSurface, fontWeight = FontWeight.SemiBold)
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                onClick = { picker.launch("image/*") }
            ) {
                val model: Any? = imageUri ?: existing?.imageUrl?.takeIf { it.isNotBlank() }
                if (model != null) {
                    AsyncImage(model, stringResource(R.string.selected_listing_photo), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.AddPhotoAlternate, null, tint = Primary)
                            Text(stringResource(R.string.select_listing_photo), color = Primary)
                        }
                    }
                }
            }
            Text(stringResource(R.string.listing_photo_helper), color = OnSurfaceVariant, fontSize = 12.sp)
            ListingTextField(description, { description = it }, R.string.listing_description, required = false, helper = R.string.listing_optional, minLines = 3)
            ListingTextField(amenities, { amenities = it }, R.string.listing_amenities, required = false, helper = R.string.listing_amenities_helper)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { viewModel.saveProperty(existing, title, price, address, beds, baths, sqft, category, description, amenities, imageUri, onSaved) },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnSurface)
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.height(20.dp), color = OnSurface) else Text(stringResource(R.string.save_listing))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ListingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    required: Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    helper: Int? = null,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(label) + if (required) " *" else "") },
        supportingText = helper?.let { { Text(stringResource(it)) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        singleLine = minLines == 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface
        )
    )
}
