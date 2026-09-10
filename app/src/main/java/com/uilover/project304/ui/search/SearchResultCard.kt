package com.uilover.project304.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.components.PropertyImage
import com.uilover.project304.ui.theme.Cream as CardBackground
import com.uilover.project304.ui.theme.Error
import com.uilover.project304.ui.theme.WarmTaupe as OnSurfaceVariant
import com.uilover.project304.ui.theme.WarmBorder as OutlineVariant
import com.uilover.project304.ui.theme.SageGreen as Primary

@Composable
fun SearchResultCard(
    property: Property,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(OutlineVariant.copy(alpha = 0.5f))
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image with Badge and Favorite
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                PropertyImage(
                    property = property,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top-Left Badge ("New Construction", "Open House")
                if (!property.badge.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xD986EFAC))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = property.badge,
                            color = Color(0xFF14532D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Top-Right Favorite Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xEBFFFFFF))
                        .clickable { onFavoriteToggle(property.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Error else Primary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Card Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Price
                Text(
                    text = property.formattedPrice,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Address
                Text(
                    text = property.address,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(
                    color = OutlineVariant.copy(alpha = 0.4f),
                    thickness = 0.8.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Spec Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Beds / Studio
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KingBed,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = property.formattedBeds,
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    // Baths
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bathtub,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = property.formattedBaths,
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    // Sqft
                    if (property.sqft > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SquareFoot,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = property.formattedSqft,
                                color = OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
