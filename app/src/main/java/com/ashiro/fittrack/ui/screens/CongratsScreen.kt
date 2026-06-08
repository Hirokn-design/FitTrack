package com.ashiro.fittrack.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.ashiro.fittrack.R
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.theme.CyanElectric

@Composable
fun CongratsScreen(
    activityName: String,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current

    // Animation Trophée (chargée une seule fois)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.congrats))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        Text(
            text = "MISSION ACCOMPLIE",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Raid '$activityName' terminé avec succès.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Actions
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SystemButton(
                text = "PARTAGER L'EXPLOIT",
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Je viens de terminer le raid '$activityName' sur FitTrack ! #FitTrack #RaidCompleted")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Partager via"))
                },
                modifier = Modifier.fillMaxWidth()
            )

            SystemButton(
                text = "RETOUR",
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth(),
                isSecondary = true
            )
        }
    }
}