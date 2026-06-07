package com.ashiro.fittrack

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ashiro.fittrack.ui.theme.CyanElectric
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // 1. Animation de fondu pour l'apparition initiale
    val alphaAnim = remember { Animatable(0f) }

    // 2. Animation de pulsation pour l'effet "Glow"
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    LaunchedEffect(key1 = true) {
        // Anime l'opacité de 0% à 100% en 1 seconde
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(2000) // Laisse le logo visible
        onTimeout()  // Signale la fin
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19)),
        contentAlignment = Alignment.Center
    ) {
        // LOGO OCCUPANT TOUT L'ÉCRAN
        Image(
            painter = painterResource(id = R.drawable.logo_fittrack), 
            contentDescription = "FitTrack Logo",
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value),
            contentScale = ContentScale.Crop // Remplit tout l'écran
        )

        // EFFET DE HALO (Glow) diffus par-dessus pour l'ambiance "Système"
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value * 0.3f * glowScale)
                .blur(50.dp)
                .background(CyanElectric.copy(alpha = 0.4f))
        )

        // ICONE DE CHARGEMENT RONDE EN BAS AU MILIEU
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = CyanElectric,
                strokeWidth = 3.dp,
                trackColor = CyanElectric.copy(alpha = 0.1f)
            )
        }
    }
}
