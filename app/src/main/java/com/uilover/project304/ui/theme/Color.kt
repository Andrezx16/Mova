package com.uilover.project304.ui.theme

import androidx.compose.ui.graphics.Color

// Global Design Tokens - Color Palette (Material 3)
val Primary = Color(0xFF1A237E)         // Brand color, main CTAs, active states, icons
val OnPrimary = Color(0xFFFFFFFF)       // Text/icons on top of Primary
val Secondary = Color(0xFF303F9F)       // Alternative actions, buttons, accent elements
val OnSecondary = Color(0xFFFFFFFF)
val Surface = Color(0xFFF8F9FA)         // Main screen background
val OnSurface = Color(0xFF1A1C1E)       // High-emphasis text (Headlines, Body)
val SurfaceVariant = Color(0xFFE1E2EC)  // Low-emphasis backgrounds (Card containers, Dividers)
val OnSurfaceVariant = Color(0xFF44474E)// Medium-emphasis text (Labels, Captions)
val Outline = Color(0xFF74777F)         // Input borders, subtle dividers
val OutlineVariant = Color(0xFFE0E2EC)  // Very subtle borders
val Error = Color(0xFFB3261E)           // Destructive actions, error states
val OnError = Color(0xFFFFFFFF)

// Semantic & Accent Colors
val GoldRating = Color(0xFFFFA000)
val BadgeFeaturedBg = Color(0xCC093121)
val BadgeFeaturedText = Color(0xFF4ADE80)
val BadgeNewBuildBg = Color(0xF2FFFFFF)
val BadgeNewBuildText = Color(0xFF1A1C1E)
val CardBackground = Color(0xFFFFFFFF)
val ScrimDark = Color(0xB3000000)
val NavIndicator = Color(0x261A237E)

// ---------------------------------------------------------------------------
// WoodNest Glassmorphism palette
// New brand system layered on top of the tokens above. Existing screens keep
// using Primary/OnSurface/etc. unchanged; only screens redesigned for the
// WoodNest look (Login, Home, Search, Property Detail, and onward) use these.
// ---------------------------------------------------------------------------
val Cream = Color(0xFFF0EAD2)       // primary light surface, primary CTA fill, text on photos
val PaleSage = Color(0xFFDDE5B6)    // secondary glass tint, soft highlights, selected states
val SageGreen = Color(0xFFADC178)   // accent, active controls, status indicators, icons
val WarmTaupe = Color(0xFFA98467)   // secondary accent, borders, warm UI details
val DeepBrown = Color(0xFF6C584C)   // primary dark text, dark surfaces, nav, contrast

val GlassSurfaceLight = Color(0x59F0EAD2)  // cream glass over dark photos - opaque enough to stay legible
val GlassSurfaceDark = Color(0xB3543F32)   // dark glass over bright photos - opaque enough to stay legible
val GlassBorder = Color(0x80F0EAD2)        // hairline glass edge
val GlassHighlight = Color(0x59FFFFFF)     // inset top highlight simulating edge refraction
val GlassShadow = Color(0x2E3C2E1E)        // rgba(60,48,38,0.18) - warm-tinted shadow, not pure black
val WarmBorder = Color(0x59A98467)         // Warm Taupe at ~35% alpha - hairline borders on light Cream surfaces