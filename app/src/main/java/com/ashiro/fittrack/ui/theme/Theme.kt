package com.ashiro.fittrack.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanElectric,         // 🌌 Mode Sombre : Bleu Néon Électrique/Fluo d'origine
    secondary = ManaPurple,
    tertiary = CyanElectric,
    background = SystemBackground,   // Fond bleu nuit
    surface = CardBackground,        // Cartes bleues sombres

    // TEXTES ET CONTENUS (MODE SOMBRE)
    onPrimary = SystemBackground,    // Texte sombre sur bouton Cyan
    onSecondary = GlassWhite,
    onTertiary = SystemBackground,
    onBackground = GlassWhite,       // Texte principal en blanc givré
    onSurface = GlassWhite,          // Texte des cartes en blanc
    surfaceVariant = SystemGray
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,          // ☀️ Mode Clair : Bleu Électrique Intense/Saturé (Color(0xFF007AFF)) pour la lisibilité
    secondary = ManaPurple,          // Ton violet
    tertiary = LightPrimary,
    background = LightSystemGray,      // Fond gris/bleu très clair (style iOS)
    surface = LightGlassBackground,   // Cartes blanches effet verre translucide

    // TEXTES ET CONTENUS (MODE CLAIR)
    onPrimary = Color.White,          // 👁️ Texte blanc pour ressortir parfaitement sur le Bleu Électrique Intense
    onSecondary = LightTextPrimary,
    onTertiary = Color.White,
    onBackground = LightTextPrimary,  // Titres et Textes généraux en Noir/Bleu profond
    onSurface = LightTextPrimary,     // Contenu des cartes claires en sombre
    surfaceVariant = CardBackground
)

@Composable
fun FitTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}