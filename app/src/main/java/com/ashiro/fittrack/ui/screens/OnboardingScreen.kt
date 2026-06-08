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
        // 1. CARROUSEL D'IMAGES À CONTRASTE OPTIMISÉ
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f // Légèrement réduit pour faire ressortir les éléments d'interface
            )
        }

        // 2. ÉLÉMENTS DE CONTRÔLE ET D'IMMERSION (HAUT DE L'ÉCRAN)
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Label de version ou d'état du Système
                Text(
                    text = "SYS.VER.2026_V3",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = CyanElectric.copy(alpha = 0.6f)
                )

                // Bouton "Passer" l'onboarding pour aller à l'essentiel, style Chasseur pressé
                if (pagerState.currentPage < images.size - 1) {
                    TextButton(onClick = onFinished) {
                        Text(
                            text = "PASSER",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // 3. BARRE DE PROGRESSION DE SYNCHRONISATION
            val progress by animateFloatAsState(
                targetValue = (pagerState.currentPage + 1).toFloat() / images.size,
                label = "system_sync"
            )

            Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                Text(
                    text = "SYNCHRONISATION DU SYSTÈME : ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                    color = CyanElectric,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
                        .border(0.5.dp, CyanElectric.copy(alpha = 0.3f), CircleShape)
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

            Spacer(modifier = Modifier.weight(1f))

            // 4. CADRE D'INTERACTION DE L'ASSISTANT AVATAR (BOÎTE FLOTTANTE)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(28.dp))
                    // Utilisation d'une couleur de surface Material3 adaptative pour supporter le mode clair
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .border(1.dp, CyanElectric.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // AVATAR ASSISTANT (Effet Holographique Néon)
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(ManaPurple.copy(alpha = 0.15f), CircleShape)
                            .border(2.dp, Brush.sweepGradient(listOf(CyanElectric, ManaPurple)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Assistant AI",
                            modifier = Modifier.size(50.dp),
                            tint = CyanElectric
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Titre de la quête / étape actuelle
                    Text(
                        text = titles[pagerState.currentPage],
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = CyanElectric,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description textuelle
                    Text(
                        text = descriptions[pagerState.currentPage],
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // INDICATEURS VISUELS DE PAGES (Pills / Dots)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        repeat(images.size) { i ->
                            val isSelected = pagerState.currentPage == i
                            Box(
                                modifier = Modifier
                                    .size(width = if (isSelected) 18.dp else 6.dp, height = 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) CyanElectric else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }

                    // BOUTON DE SÉLECTION D'ÉTAPES / ÉVEIL
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
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}