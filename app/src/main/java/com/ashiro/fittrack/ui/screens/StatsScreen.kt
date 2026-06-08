package com.ashiro.fittrack.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.ui.components.SystemCard
import kotlin.math.pow

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)

    // Récupération des données du Système
    val weight = prefs.getInt("WEIGHT", 0).toFloat()
    val heightCm = prefs.getInt("HEIGHT", 0).toFloat()
    val steps = prefs.getInt("DAILY_STEPS", 0)
    val activityCount = prefs.getInt("COMPLETED_MISSIONS", 0)

    val hasData = weight > 0 && heightCm > 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "REGISTRE D'ÉVOLUTION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "STATISTIQUES DE FITNESS",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (!hasData && steps == 0) {
            // ÉTAT VIDE : Pas encore d'activité
            item {
                SystemCard(modifier = Modifier.padding(top = 40.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AUCUNE DONNÉE DÉTECTÉE",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Commencez une activité ou une quête pour initialiser vos statistiques de puissance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            // SECTION : ANALYSE DE PUISSANCE (Dynamique)
            item {
                val heightM = heightCm / 100f
                val imc = if (heightM > 0) weight / heightM.pow(2) else 0f

                // Formule de Puissance "Système"
                val powerScore = (steps * 0.1f) + (activityCount * 50f) + (if (imc in 18.5..25.0) 100f else 50f)
                val rank = when {
                    powerScore > 2000 -> "RANG S"
                    powerScore > 1000 -> "RANG A"
                    powerScore > 500 -> "RANG B"
                    else -> "RANG E"
                }

                SystemCard {
                    Text(
                        text = "SCORE DE PUISSANCE : ${powerScore.toInt()}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
                    ) {
                        val progress = (powerScore / 2500f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                                    ),
                                    CircleShape
                                )
                        )
                    }

                    Text(
                        text = "ÉVALUATION : $rank",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            // SECTION : CALENDRIER D'ACTIVITÉ (Suivi de progrès)
            item {
                SystemCard {
                    Text(
                        text = "REGISTRE DES ACTIVITÉS (28 DERNIERS JOURS)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Grille technologique
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (row in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val dayIndex = row * 7 + col
                                    val isActive = dayIndex < (activityCount + 5) % 28

                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                                RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Légende : Activité effectuée / En attente",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // SECTION : RÉCAPITULATIF
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatItemCard("PAS TOTAUX", steps.toString(), MaterialTheme.colorScheme.primary)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatItemCard("MISSIONS", activityCount.toString(), MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItemCard(label: String, value: String, accentColor: Color) {
    SystemCard {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = accentColor
        )
    }
}