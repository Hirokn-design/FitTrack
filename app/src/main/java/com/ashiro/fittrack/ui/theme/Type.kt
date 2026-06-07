package com.ashiro.fittrack.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Configuration Typographique "Système"
val Typography = Typography(
    // Titres de sections (Effet "Message du Système")
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 2.sp, // Espacement élargi pour le look tech
    ),
    // Texte de contenu classique
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = SystemGray
    ),
    // Labels et petits textes (ex: Stats)
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace, // Monospace donne un côté "code" ou "data"
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
        color = CyanElectric
    )
)