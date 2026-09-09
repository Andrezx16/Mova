package com.uilover.project304.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project304.data.model.UserProfile
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OutlineVariant
import com.uilover.project304.ui.theme.Primary

@Composable
fun LuxeTopBar(
    userProfile: UserProfile? = null,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Navigation Menu",
                tint = OnSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "LUXE REALTY",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Primary
        )

        // Spacer to balance the top bar and keep title centered
        Spacer(modifier = Modifier.size(40.dp))
    }
}

