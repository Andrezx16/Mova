package com.uilover.project304.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
