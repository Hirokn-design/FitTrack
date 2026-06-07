package com.ashiro.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashiro.fittrack.ui.components.SystemCard
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.ManaPurple

data class Meal(val time: String, val name: String, val calories: Int, val description: String)

@Composable
fun NutritionScreen() {
    val dailyMeals = listOf(
        Meal("08:00", "PETIT DÉJEUNER", 450, "Omelette, Avocat, Pain complet"),
        Meal("12:30", "DÉJEUNER PROTÉINÉ", 700, "Poulet grillé, Riz brun, Brocolis"),
        Meal("16:00", "GOUTER DE RÉCUPÉRATION", 250, "Shake de protéines, Banane"),
        Meal("20:00", "DINER ÉQUILIBRÉ", 500, "Saumon, Patate douce, Salade")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "PLAN DE MANA",
                style = MaterialTheme.typography.labelMedium,
                color = CyanElectric
            )
            Text(
                text = "ALIMENTATION & ÉNERGIE",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        // Résumé des Macros
        item {
            SystemCard {
                Text(text = "RÉPARTITION DES MACROS", style = MaterialTheme.typography.labelMedium, color = CyanElectric)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MacroItem("PROTÉINES", "160g", ManaPurple)
                    MacroItem("GLUCIDES", "220g", CyanElectric)
                    MacroItem("LIPIDES", "70g", Color.Yellow)
                }
            }
        }

        // Liste des repas
        item {
            Text(text = "REPAS DU JOUR", style = MaterialTheme.typography.labelMedium, color = CyanElectric)
        }

        items(dailyMeals) { meal ->
            SystemCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = meal.time, style = MaterialTheme.typography.labelSmall, color = CyanElectric)
                        Text(text = meal.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = meal.description, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    }
                    Text(text = "${meal.calories}\nKCAL", style = MaterialTheme.typography.titleMedium, color = ManaPurple, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
    }
}
