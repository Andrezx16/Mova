package com.uilover.project304.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uilover.project304.R
import com.uilover.project304.ui.theme.Cream
import com.uilover.project304.ui.theme.DeepBrown
import com.uilover.project304.ui.theme.GlassBorder
import com.uilover.project304.ui.theme.GlassHighlight
import com.uilover.project304.ui.theme.GlassShadow
import com.uilover.project304.ui.theme.GlassSurfaceDark
import com.uilover.project304.ui.theme.GlassSurfaceLight
import com.uilover.project304.ui.theme.QuicksandFamily
import com.uilover.project304.ui.theme.SageGreen

/**
 * WoodNest glassmorphism surface: a translucent, bordered card meant to sit on top of
 * photography. True backdrop blur needs API 31 (RenderEffect) which is above this app's
 * minSdk 24, so the "glass" here is approximated with layered translucency, a soft warm
 * shadow and a thin light border rather than an actual blur of what's behind it.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    dark: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 18.dp, shape = shape, ambientColor = GlassShadow, spotColor = GlassShadow)
            .clip(shape)
            .background(if (dark) GlassSurfaceDark else GlassSurfaceLight)
            .border(1.dp, GlassBorder, shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

/** Full-bleed nature photography backdrop with a bottom-weighted scrim for text legibility. */
@Composable
fun NatureBackdrop(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x99140F0A),
                            0.5f to Color(0xB8140F0A),
                            1.0f to Color(0xF2140F0A)
                        )
                    )
                )
        )
    }
}

/** Primary CTA: Cream fill, Deep Brown text, fully rounded pill. */
@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(if (enabled) Cream else Cream.copy(alpha = 0.5f))
            .border(1.dp, GlassHighlight, RoundedCornerShape(50))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = DeepBrown,
            fontFamily = QuicksandFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

/** Secondary action: translucent glass pill with light text, for use over photography. */
@Composable
fun GlassSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(if (dark) GlassSurfaceDark else GlassSurfaceLight)
            .border(1.dp, GlassBorder, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (dark) Cream else DeepBrown,
            fontFamily = QuicksandFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

/** Small rounded glass chip used for badges, filters and status labels. */
@Composable
fun GlassPill(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    dark: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .background(
                when {
                    selected -> SageGreen
                    dark -> GlassSurfaceDark
                    else -> GlassSurfaceLight
                }
            )
            .border(1.dp, if (selected) SageGreen else GlassBorder, RoundedCornerShape(50))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) DeepBrown else if (dark) Cream else DeepBrown,
            fontFamily = QuicksandFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}

enum class MovaLogoVariant { MARK, WORDMARK }

/** MOVA brand logo. MARK is the standalone "M" icon for nav/chrome; WORDMARK is the full lockup for Login/SignUp. */
@Composable
fun MovaLogo(
    variant: MovaLogoVariant,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = if (variant == MovaLogoVariant.MARK) R.drawable.logo_app else R.drawable.logo_login
        ),
        contentDescription = "MOVA",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

/** Circular glass icon button container (back arrow, share, favorite, etc. over photography). */
@Composable
fun GlassIconButton(
    size: Dp = 40.dp,
    dark: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (dark) GlassSurfaceDark else GlassSurfaceLight)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
