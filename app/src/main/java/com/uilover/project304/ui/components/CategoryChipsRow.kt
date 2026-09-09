package com.uilover.project304.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.ui.theme.CardBackground
import com.uilover.project304.ui.theme.OnPrimary
import com.uilover.project304.ui.theme.OnSurface
import com.uilover.project304.ui.theme.OutlineVariant
import com.uilover.project304.ui.theme.Primary

@Composable
fun CategoryChipsRow(
    selectedCategory: PropertyCategory,
    onCategorySelected: (PropertyCategory) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<PropertyCategory> = PropertyCategory.values().toList()
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Primary else CardBackground,
                label = "chipBgColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) OnPrimary else OnSurface,
                label = "chipTextColor"
            )
            val borderColor = if (isSelected) Color.Transparent else OutlineVariant

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onCategorySelected(category)
                    }
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(category.labelRes),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}
