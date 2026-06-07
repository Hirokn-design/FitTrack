package com.ashiro.fittrack.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.ui.components.SystemCard
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.ManaPurple

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)

    val name = prefs.getString("USERNAME", "CHASSEUR INCONNU") ?: "CHASSEUR INCONNU"
    val rank = "RANG E" 

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER : TITRE DU SYSTÈME AVEC BOUTON RETOUR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Retour", 
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "STATISTIQUES DU CHASSEUR",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White
                        )
                        HorizontalDivider(
                            modifier = Modifier.width(150.dp).padding(top = 4.dp),
                            thickness = 2.dp,
                            color = CyanElectric
                        )
                    }
                }
            }

            // BLOC IDENTITÉ
            item {
                SystemCard {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, CyanElectric, CircleShape)
                                    .background(CyanElectric.copy(alpha = 0.1f))
                            )
                            Icon(Icons.Default.Person, contentDescription = null, tint = CyanElectric, modifier = Modifier.size(60.dp))
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Column {
                            Text(text = name.uppercase(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(text = "RANG : $rank", style = MaterialTheme.typography.labelSmall, color = CyanElectric)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Icônes Core stables
                            ProfileSmallStat(Icons.Default.KeyboardArrowUp, "${prefs.getInt("HEIGHT", 0)} CM")
                            ProfileSmallStat(Icons.Default.Info, "${prefs.getInt("WEIGHT", 0)} KG")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Jauge d'XP
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "EXP", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(text = "75%", style = MaterialTheme.typography.labelSmall, color = CyanElectric)
                    }
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = CyanElectric,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }

            // SKILL TRACKER
            item {
                SystemCard {
                    Text("REGISTRE DES CAPACITÉS", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp), color = CyanElectric)
                    Spacer(modifier = Modifier.height(16.dp))
                    SkillItem("FORCE", 0.85f)
                    SkillItem("AGILITÉ", 0.70f)
                    SkillItem("ENDURANCE", 0.90f)
                    SkillItem("INTELLIGENCE", 0.50f)
                }
            }

            // HABIT TRACKER (Graph)
            item {
                SystemCard {
                    Text("HISTORIQUE D'ACTIVITÉ", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp), color = CyanElectric)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, 0.7f).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .fillMaxHeight(h)
                                    .background(CyanElectric.copy(alpha = 0.6f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .border(0.5.dp, CyanElectric, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSmallStat(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icon, contentDescription = null, tint = CyanElectric, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
    }
}

@Composable
fun SkillItem(label: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = CyanElectric)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = ManaPurple,
            trackColor = Color.White.copy(alpha = 0.05f)
        )
    }
}
