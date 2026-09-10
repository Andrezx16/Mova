package com.uilover.project304.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.theme.AntonFamily
import com.uilover.project304.ui.theme.Cream
import com.uilover.project304.ui.theme.DeepBrown
import com.uilover.project304.ui.theme.GlassBorder
import com.uilover.project304.ui.theme.GlassShadow
import com.uilover.project304.ui.theme.GlassSurfaceDark
import com.uilover.project304.ui.theme.GoldRating
import com.uilover.project304.ui.theme.QuicksandFamily
import com.uilover.project304.ui.theme.SageGreen

@Composable
fun RecommendedPropertyCard(
    property: Property,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = shape, ambientColor = GlassShadow, spotColor = GlassShadow)
            .clip(shape)
            .background(GlassSurfaceDark)
            .border(1.dp, GlassBorder, shape)
            .clickable { onClick() }
    ) {
        // Image with Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            PropertyImage(
                property = property,
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top-right Favorite Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceDark)
                    .border(1.dp, GlassBorder, CircleShape)
                    .clickable { onFavoriteToggle(property.id) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) SageGreen else Cream,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Bottom-left Badge (e.g. "New Build")
            if (!property.badge.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = property.badge,
                        color = DeepBrown,
                        fontFamily = QuicksandFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Card Body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Price and Rating Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.formattedPrice,
                    color = Cream,
                    fontFamily = AntonFamily,
                    fontSize = 19.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldRating,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "%.1f".format(property.rating),
                        color = Cream,
                        fontFamily = QuicksandFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Title
            Text(
                text = property.title,
                color = Cream,
                fontFamily = QuicksandFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Address
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Cream.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = property.address,
                    color = Cream.copy(alpha = 0.7f),
                    fontFamily = QuicksandFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                color = GlassBorder,
                thickness = 0.8.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Row (Beds, Baths, Sqft)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Beds
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KingBed,
                        contentDescription = null,
                        tint = Cream.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = property.formattedBeds,
                        color = Cream.copy(alpha = 0.7f),
                        fontFamily = QuicksandFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
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
                        tint = Cream.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = property.formattedBaths,
                        color = Cream.copy(alpha = 0.7f),
                        fontFamily = QuicksandFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
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
                            tint = Cream.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = property.formattedSqft,
                            color = Cream.copy(alpha = 0.7f),
                            fontFamily = QuicksandFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
