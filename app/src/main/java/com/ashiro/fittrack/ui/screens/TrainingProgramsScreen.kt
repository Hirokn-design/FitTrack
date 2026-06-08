package com.ashiro.fittrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.ui.components.TrainingSessionCard
import com.ashiro.fittrack.ui.components.glassEffect
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.LightPrimary

data class WorkoutProgram(
    val title: String,
    val duration: String,
    val exercisesCount: Int,
    val calories: Int,
    val muscles: String,
    val instructions: List<String>,
    val imageRes: Int
)

data class WorkoutCategory(
    val name: String,
    val programs: List<WorkoutProgram>
)

@Composable
fun TrainingProgramsScreen(onStartTraining: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()
    
    val categories = listOf(
        WorkoutCategory(
            name = "MUSCULATION & RENFORCEMENT",
            programs = listOf(
                WorkoutProgram("Haut du Corps Sculpté", "5 min", 3, 450, "Pec/Dos/Bras", listOf("Push-ups", "Dips en appui", "Jump Rope"), R.drawable.sculpt),
                WorkoutProgram("Puissance Bas du Corps", "3 min", 3, 500, "Jambes/Fessiers", listOf("Squats explosifs", "Fentes marchées", "Chaise Invisible"), R.drawable.bas),
                WorkoutProgram("Gainage & Force Abdominale", "5 min", 3, 200, "Abdos/Lombaires", listOf("Planche rotative", "Crunchs abdominaux", "Twist russe"), R.drawable.gen)
            )
        ),
        WorkoutCategory(
            name = "CARDIO & HAUTE INTENSITÉ",
            programs = listOf(
                WorkoutProgram("HIIT Explosif", "5 min", 3, 600, "Full Body", listOf("Burpees", "Jumping Jacks", "Étirement latéral"), R.drawable.burn),
                WorkoutProgram("Brûle-Graisses Express", "5 min", 3, 700, "Endurance", listOf("Corde à Sauter", "Jumping Jacks", "Twist russe"), R.drawable.e_rank)
            )
        ),
        WorkoutCategory(
            name = "BIEN-ÊTRE & RÉCUPÉRATION",
            programs = listOf(
                WorkoutProgram("Réveil Musculaire Doux", "8 min", 3, 100, "Mobilité", listOf("Squats", "Ouverture de hanche", "Split jump"), R.drawable.pos),
                WorkoutProgram("Mobilité & Flexibilité", "10 min", 4, 120, "Articulations", listOf("Split jump", "Ouverture hanches", "Jumping Jacks", "Split jump"), R.drawable.mobility),
                WorkoutProgram("Stretching Profond", "3 min", 2, 80, "Récupération", listOf("Étirements Ischios", "Flexion Avant"), R.drawable.stre)
            )
        ),
        WorkoutCategory(
            name = "RAIDS SPÉCIAUX",
            programs = listOf(
                WorkoutProgram("Agilité & Vitesse", "15 min", 4, 300, "Agilité", listOf("Sprints", "Corde à Sauter", "Twist russe", "Petit Saut"), R.drawable.speed2),
                WorkoutProgram("Éveil du Monarque", "20 min", 4, 1850, "Elite", listOf("Entraînement combiné Rang S"), R.drawable.eveil_mon)
            )
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = "REGISTRE DES ACTIVITÉS",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                    color = if (isDark) CyanElectric else LightPrimary
                )
                Text(
                    text = "SÉLECTIONNEZ VOTRE RAID",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = if (isDark) Color.White else Color.Black
                )
            }
        }

        items(categories) { category ->
            ExpandableCategory(category, onStartTraining)
        }
        
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
fun ExpandableCategory(category: WorkoutCategory, onStartTraining: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassEffect(RoundedCornerShape(20.dp), borderAlpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isExpanded) (if (isDark) CyanElectric else LightPrimary) else (if (isDark) Color.White else Color.Black)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (isExpanded) (if (isDark) CyanElectric else LightPrimary) else Color.Gray
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                category.programs.forEach { program ->
                    TrainingSessionCard(
                        title = program.title,
                        duration = program.duration,
                        exercisesCount = program.exercisesCount,
                        calories = program.calories,
                        muscles = program.muscles,
                        instructions = program.instructions,
                        imageRes = program.imageRes,
                        onStart = { onStartTraining(program.title) }
                    )
                }
            }
        }
    }
}
