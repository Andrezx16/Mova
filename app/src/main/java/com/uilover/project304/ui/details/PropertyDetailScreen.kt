package com.uilover.project304.ui.details

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uilover.project304.R
import com.uilover.project304.ui.components.PropertyImage
import com.uilover.project304.ui.theme.AntonFamily
// WoodNest palette, aliased onto this screen's existing color tokens so every
// Text/Icon call below picks up the new palette without a full-file rewrite.
import com.uilover.project304.ui.theme.Cream as CardBackground
import com.uilover.project304.ui.theme.Error
import com.uilover.project304.ui.theme.DeepBrown as OnPrimary
import com.uilover.project304.ui.theme.DeepBrown as OnSurface
import com.uilover.project304.ui.theme.WarmTaupe as OnSurfaceVariant
import com.uilover.project304.ui.theme.WarmBorder as OutlineVariant
import com.uilover.project304.ui.theme.SageGreen as Primary
import com.uilover.project304.ui.theme.Cream as Surface

@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onBackClick: () -> Unit,
    onScheduleTourClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(propertyId) { viewModel.loadProperty(propertyId) }

    val propertyState by viewModel.property.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    if (propertyState == null) {
        Box(
            modifier = modifier.fillMaxSize().background(Surface),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }
    val property = propertyState!!

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        bottomBar = {
            // Fixed Bottom Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp),
                color = CardBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.ownerId != viewModel.currentUserId) OutlinedButton(
                        onClick = { onScheduleTourClick(property.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.schedule_tour),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }

                    // Contact Filled Button
                    Button(
                        onClick = {
                            Toast.makeText(context, context.getString(R.string.contacting_agent, property.title), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.contact),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnPrimary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Box - Extends to the very top edge behind transparent status bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                // Single Specific Property Image
                PropertyImage(
                    property = property,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle top gradient scrim for status bar clarity
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x55000000),
                                    Color.Transparent
                                )
                            )
                        )
                )


                // Floating Action Header (Back, Share, Heart)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xCCF0EAD2))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Right Actions (Share + Favorite)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Share Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xCCF0EAD2))
                                .clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text, property.title, property.address, property.formattedPrice))
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_property)))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Favorite Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xCCF0EAD2))
                                .clickable { viewModel.toggleFavorite() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Error else OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }


            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                // "NEW LISTING" Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(com.uilover.project304.ui.theme.SageGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_listing_badge),
                        color = com.uilover.project304.ui.theme.DeepBrown,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = property.title,
                    fontFamily = AntonFamily,
                    fontSize = 26.sp,
                    color = OnSurface,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price
                Text(
                    text = property.formattedPrice,
                    fontFamily = AntonFamily,
                    fontSize = 22.sp,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = property.address,
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3-Stat Spec Cards Row (Beds, Baths, Sqft)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatCard(
                        icon = Icons.Outlined.KingBed,
                        value = property.beds.toString(),
                        label = stringResource(R.string.stat_beds),
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        icon = Icons.Outlined.Bathtub,
                        value = if (property.baths % 1.0 == 0.0) property.baths.toInt().toString() else property.baths.toString(),
                        label = stringResource(R.string.stat_baths),
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        icon = Icons.Outlined.SquareFoot,
                        value = "%,d".format(if (property.sqft > 0) property.sqft else 4500),
                        label = stringResource(R.string.stat_sqft),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // "About this property" Section
                Text(
                    text = stringResource(R.string.about_property),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                val defaultDesc = "Experience the pinnacle of luxury living in this architectural masterpiece nestled in the heart of Beverly Hills. The Beverly Modern offers seamless indoor-outdoor living with floor-to-ceiling glass walls that retract to reveal stunning panoramic views of the city skyline.\n\nMeticulously designed with premium finishes throughout, including imported Italian marble, custom European cabinetry, and state-of-the-art smart home technology. The expansive master suite features a private terrace, dual walk-in closets, and a spa-like en-suite bath designed for ultimate relaxation."
                val desc = if (property.description.isNotBlank()) "${property.description}\n\n$defaultDesc" else defaultDesc

                Text(
                    text = desc,
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 22.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 6,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.animateContentSize()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isExpanded) stringResource(R.string.read_less) else stringResource(R.string.read_more),
                    color = Primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // "Amenities" Section
                Text(
                    text = stringResource(R.string.amenities_section),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AmenityPillItem(
                            icon = Icons.Outlined.Pool,
                            title = stringResource(R.string.pool),
                            modifier = Modifier.weight(1f)
                        )
                        AmenityPillItem(
                            icon = Icons.Outlined.FitnessCenter,
                            title = stringResource(R.string.gym),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AmenityPillItem(
                            icon = Icons.Outlined.LocalParking,
                            title = stringResource(R.string.parking),
                            modifier = Modifier.weight(1f)
                        )
                        AmenityPillItem(
                            icon = Icons.Outlined.Wifi,
                            title = stringResource(R.string.wifi),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "Location" Section
                Text(
                    text = stringResource(R.string.location_section),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location Map Preview Card
                LocationMapPreview(
                    address = property.address,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineVariant.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun AmenityPillItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(com.uilover.project304.ui.theme.PaleSage.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        }
    }
}

@Composable
private fun LocationMapPreview(
    address: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(com.uilover.project304.ui.theme.PaleSage.copy(alpha = 0.4f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        // Decorative map canvas drawing streets/grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Background roads grid
            val roadColor = Color(0xFFC9C0A6)
            val mainRoadColor = Color(0xFFB8AC87)

            // Horizontal roads
            drawLine(roadColor, Offset(0f, canvasHeight * 0.25f), Offset(canvasWidth, canvasHeight * 0.25f), strokeWidth = 8f)
            drawLine(mainRoadColor, Offset(0f, canvasHeight * 0.55f), Offset(canvasWidth, canvasHeight * 0.55f), strokeWidth = 14f)
            drawLine(roadColor, Offset(0f, canvasHeight * 0.8f), Offset(canvasWidth, canvasHeight * 0.8f), strokeWidth = 8f)

            // Vertical roads
            drawLine(roadColor, Offset(canvasWidth * 0.2f, 0f), Offset(canvasWidth * 0.2f, canvasHeight), strokeWidth = 8f)
            drawLine(mainRoadColor, Offset(canvasWidth * 0.5f, 0f), Offset(canvasWidth * 0.5f, canvasHeight), strokeWidth = 16f)
            drawLine(roadColor, Offset(canvasWidth * 0.78f, 0f), Offset(canvasWidth * 0.78f, canvasHeight), strokeWidth = 8f)

            // Diagonal avenues
            drawLine(roadColor, Offset(0f, 0f), Offset(canvasWidth * 0.8f, canvasHeight), strokeWidth = 6f)
        }

        // Center Location Pin with pulse effect
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .border(2.5.dp, Color.White, CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Property Marker",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Bottom Map Address Overlay Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xF5F0EAD2))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
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
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = address,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                }

                Text(
                    text = "Open in Maps",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}
