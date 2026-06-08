package com.ashiro.fittrack

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.SystemBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.7f) }

    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.system_loader)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        delay(3500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemBackground)
    ) {
        // Arrière-plan
        Image(
            painter = painterResource(id = R.drawable.bg_init),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.1f),
            contentScale = ContentScale.Crop
        )

        // 1. ZONE LOGO (Haut de l'écran - Agrandi x1.5)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp) // Remonté pour compenser la grande taille
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_fittrack),
                contentDescription = "Logo",
                modifier = Modifier.size(360.dp) // 240dp * 1.5 = 360dp
            )
        }

        // 2. ZONE DE CHARGEMENT (Bas de l'écran)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp), // Remonté légèrement pour laisser plus de place
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = lottieComposition,
                progress = { lottieProgress },
                // Augmentation de la taille ici
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "INITIALISATION DU SYSTÈME",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 5.sp
                ),
                color = CyanElectric
            )
        }
    }
}