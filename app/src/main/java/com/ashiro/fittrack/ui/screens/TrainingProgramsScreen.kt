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
                WorkoutProgram("Haut du Corps Sculpté", "45 min", 12, 450, "Pec/Dos/Bras", listOf("Pompes de rang S", "Tractions de l'ombre", "Développé militaire"), R.drawable.sculpt),
                WorkoutProgram("Puissance Bas du Corps", "40 min", 10, 500, "Jambes/Fessiers", listOf("Squats explosifs", "Fentes marchées", "Soulevé de terre"), R.drawable.bas),
                WorkoutProgram("Gainage & Force Abdominale", "20 min", 8, 200, "Abdos/Lombaires", listOf("Planche de Mana", "Crunchs", "Russian Twist"), R.drawable.gen)
            )
        ),
        WorkoutCategory(
            name = "CARDIO & HAUTE INTENSITÉ",
            programs = listOf(
                WorkoutProgram("HIIT Explosif", "30 min", 15, 600, "Full Body", listOf("Burpees", "Jumping Jacks", "Mountain Climbers"), R.drawable.burn),
                WorkoutProgram("Brûle-Graisses Express", "45 min", 12, 700, "Endurance", listOf("Course fractionnée", "Corde à sauter"), R.drawable.e_rank)
            )
        ),
        WorkoutCategory(
            name = "BIEN-ÊTRE & RÉCUPÉRATION",
            programs = listOf(
                WorkoutProgram("Réveil Musculaire Doux", "15 min", 6, 100, "Mobilité", listOf("Rotations articulaires", "Étirements légers"), R.drawable.pos),
                WorkoutProgram("Mobilité & Flexibilité", "20 min", 10, 120, "Articulations", listOf("Ouverture hanches", "Mobilité épaules"), R.drawable.mobility),
                WorkoutProgram("Stretching Profond", "25 min", 8, 80, "Récupération", listOf("Étirements statiques", "Respiration"), R.drawable.stre)
            )
        ),
        WorkoutCategory(
            name = "RAIDS SPÉCIAUX",
            programs = listOf(
                WorkoutProgram("Agilité & Vitesse", "15 min", 10, 300, "Agilité", listOf("Sprints", "Shadow Boxing"), R.drawable.speed2),
                WorkoutProgram("Éveil du Monarque", "30 min", 20, 850, "Elite", listOf("Entraînement combiné Rang S"), R.drawable.eveil_mon)
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
                    text = "REGISTRE DES DONJONS",
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
