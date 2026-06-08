package com.ashiro.fittrack.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.ui.components.SystemCard
import com.ashiro.fittrack.ui.components.TrainingSessionCard
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.LightPrimary
import com.ashiro.fittrack.ui.theme.LightTextPrimary
import com.ashiro.fittrack.ui.theme.ManaPurple
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.util.UUID

// Modèle de donnée pour une quête
data class HunterQuest(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

@Composable
fun MainDashboard(username: String, steps: Int, onStartTraining: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()

    // 🛠️ Initialisation des outils de persistance locale
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("fittrack_system_prefs", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    // État de la liste chargé depuis la mémoire ou initialisé par défaut
    var quests by remember {
        mutableStateOf(
            run {
                val savedQuestsJson = sharedPrefs.getString("saved_hunter_quests", null)
                if (!savedQuestsJson.isNullOrEmpty()) {
                    try {
                        val type = object : TypeToken<List<HunterQuest>>() {}.type
                        gson.fromJson<List<HunterQuest>>(savedQuestsJson, type)
                    } catch (e: Exception) {
                        getInitialQuests()
                    }
                } else {
                    getInitialQuests()
                }
            }
        )
    }

    // 💾 Sauvegarde réactive
    LaunchedEffect(quests) {
        val jsonString = gson.toJson(quests)
        sharedPrefs.edit().putString("saved_hunter_quests", jsonString).apply()
    }

    // État pour l'ajout d'une nouvelle quête
    var newQuestTitle by remember { mutableStateOf("") }
    var isAddingQuest by remember { mutableStateOf(false) }

    // Animation de rotation pour le bouton d'ajout
    val rotationAngle by animateFloatAsState(
        targetValue = if (isAddingQuest) 135f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "addButtonRotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // HEADER : Statistiques du Chasseur
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "REGISTRE DES QUÊTES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = if (isDark) CyanElectric else LightPrimary
                )
                Text(
                    text = "Bonjour ${username.uppercase()}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    ),
                    color = if (isDark) Color.White else LightTextPrimary
                )
            }
        }

        // COMPTEUR DE PAS
        item {
            SystemCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = if (isDark) CyanElectric else LightPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "PAS AUJOURD'HUI",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color.Gray else Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$steps / 10,000",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else LightTextPrimary
                )

                val stepProgress = (steps.toFloat() / 10000f).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { stepProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).padding(top = 8.dp),
                    color = if (isDark) CyanElectric else LightPrimary,
                    trackColor = (if (isDark) Color.White else Color.Black).copy(alpha = 0.1f)
                )
            }
        }

        // ⚔️ MISSION SUGGÉRÉE
        item {
            Text(
                text = "MISSION SUGGÉRÉE",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) CyanElectric else LightPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TrainingSessionCard(
                title = "Éveil du Monarque",
                duration = "30 min",
                exercisesCount = 3,
                calories = 850,
                muscles = "Elite",
                instructions = listOf("Shadow Boxing", "Planche Commando", "Burpees"),
                imageRes = com.ashiro.fittrack.R.drawable.eveil_mon,
                onStart = { onStartTraining("Éveil du Monarque") }
            )
        }

        // SECTION QUÊTES
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MISSIONS EN COURS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyanElectric else LightPrimary
                )
                IconButton(
                    onClick = {
                        isAddingQuest = !isAddingQuest
                        if (!isAddingQuest) newQuestTitle = ""
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(rotationAngle)
                        .background(
                            (if (isAddingQuest) Color.Red else (if (isDark) CyanElectric else LightPrimary)).copy(alpha = 0.15f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isAddingQuest) "Annuler" else "Nouvelle Mission",
                        tint = if (isAddingQuest) Color.Red else (if (isDark) CyanElectric else LightPrimary),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isAddingQuest) {
            item {
                SystemCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newQuestTitle,
                            onValueChange = { newQuestTitle = it },
                            label = { Text("TITRE DE LA MISSION", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color.White else LightTextPrimary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) CyanElectric else LightPrimary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newQuestTitle.isNotBlank()) {
                                    quests = listOf(HunterQuest(title = newQuestTitle)) + quests
                                    newQuestTitle = ""
                                    isAddingQuest = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) ManaPurple else LightPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("FIXER", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // LISTE DES QUÊTES ACTIVES
        val activeQuests = quests.filter { !it.isCompleted }
        items(activeQuests, key = { it.id }) { quest ->
            QuestItem(
                quest = quest,
                isDark = isDark,
                onToggle = {
                    quests = quests.map { if (it.id == quest.id) it.copy(isCompleted = true) else it }
                }
            )
        }

        // SECTION MISSIONS ACCOMPLIES
        val completedQuests = quests.filter { it.isCompleted }
        if (completedQuests.isNotEmpty()) {
            item {
                Text(
                    text = "ARCHIVES DES MISSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            items(completedQuests, key = { it.id }) { quest ->
                QuestItem(
                    quest = quest,
                    isDark = isDark,
                    onToggle = {
                        quests = quests.map { if (it.id == quest.id) it.copy(isCompleted = false) else it }
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
fun QuestItem(quest: HunterQuest, isDark: Boolean, onToggle: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
        enter = fadeIn() + expandVertically()
    ) {
        SystemCard(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = quest.title,
                    color = if (quest.isCompleted) Color.Gray else (if (isDark) Color.White else LightTextPrimary),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (quest.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = if (quest.isCompleted) FontWeight.Normal else FontWeight.Medium
                    )
                )
                Checkbox(
                    checked = quest.isCompleted,
                    onCheckedChange = { isVisible = false },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = if (isDark) CyanElectric else LightPrimary,
                        checkedColor = if (isDark) CyanElectric else LightPrimary,
                        checkmarkColor = if (isDark) Color.Black else Color.White
                    )
                )
            }
        }
    }

    if (!isVisible) {
        LaunchedEffect(Unit) {
            delay(300)
            onToggle()
            isVisible = true
        }
    }
}

private fun getInitialQuests(): List<HunterQuest> = listOf(
    HunterQuest(title = "Hydratation (1L/2L)"),
    HunterQuest(title = "Étirements matinaux"),
    HunterQuest(title = "Méditation (10min/30min)"),
    HunterQuest(title = "Aérobic (10min/30min)")
)
