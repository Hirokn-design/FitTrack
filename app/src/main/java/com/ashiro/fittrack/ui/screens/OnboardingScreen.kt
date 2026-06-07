package com.ashiro.fittrack.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.ManaPurple
import com.ashiro.fittrack.ui.theme.SystemBackground
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val images = listOf(R.drawable.bg0, R.drawable.bg1, R.drawable.bg2)
    val titles = listOf(
        "ÉVEILLEZ VOTRE POTENTIEL",
        "DOMINEZ VOS LIMITES",
        "FORGEZ VOTRE LÉGENDE"
    )
    val descriptions = listOf(
        "Le Système a détecté un nouveau porteur. Initialisation du protocole de suivi FitTrack.",
        "Transformez chaque séance en quête de rang S. Le monde virtuel devient votre terrain de jeu.",
        "Optimisez vos statistiques corporelles grâce à l'assistant d'entraînement intelligent."
    )

    val pagerState = rememberPagerState(pageCount = { images.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(SystemBackground)) {
        // 1. CARROUSEL D'IMAGES (Défilement horizontal)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )
        }

        // 2. BARRE DE PROGRESSION STYLISÉE (EN HAUT)
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(60.dp))
            
            val progress by animateFloatAsState(
                targetValue = (pagerState.currentPage + 1).toFloat() / images.size,
                label = "system_sync"
            )
            
            Column(modifier = Modifier.padding(horizontal = 40.dp)) {
                Text(
                    text = "SYNCHRONISATION DU SYSTÈME : ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanElectric,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, CyanElectric.copy(alpha = 0.2f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(CyanElectric, ManaPurple)),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. TITRE BIENVENU STYLISÉ
            Text(
                text = "BIENVENUE DANS\nFITTRACK",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    lineHeight = 40.sp,
                    brush = Brush.verticalGradient(listOf(Color.White, CyanElectric))
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 4. CADRE DE L'ASSISTANT AVATAR ET TEXTES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, CyanElectric.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // AVATAR ASSISTANT 2D (Style Hologramme)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(ManaPurple.copy(alpha = 0.1f), CircleShape)
                            .border(2.dp, Brush.sweepGradient(listOf(CyanElectric, ManaPurple)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icône d'assistant (Avatar 2D placeholder)
                        Icon(
                            imageVector = Icons.Default.Face, 
                            contentDescription = "Assistant AI",
                            modifier = Modifier.size(60.dp),
                            tint = CyanElectric
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = titles[pagerState.currentPage],
                        style = MaterialTheme.typography.titleMedium,
                        color = CyanElectric,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = descriptions[pagerState.currentPage],
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SystemButton(
                        text = if (pagerState.currentPage == images.size - 1) "S'ÉVEILLER" else "SUIVANT",
                        onClick = {
                            if (pagerState.currentPage < images.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onFinished()
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
