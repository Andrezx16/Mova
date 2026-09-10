package com.uilover.project304.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.uilover.project304.data.model.Property
import com.uilover.project304.ui.theme.OnSurfaceVariant
import com.uilover.project304.ui.theme.OutlineVariant

/**
 * Renders a property's image from the Cloudinary/Firestore [Property.imageUrl],
 * falling back to a neutral placeholder when no image is set.
 */
@Composable
fun PropertyImage(
    property: Property,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (property.hasImageUrl) {
        AsyncImage(
            model = property.imageUrl,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(OutlineVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = contentDescription,
                tint = OnSurfaceVariant
            )
        }
    }
}
