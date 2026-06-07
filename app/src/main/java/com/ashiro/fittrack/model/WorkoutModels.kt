package com.ashiro.fittrack.model

import com.ashiro.fittrack.R

data class Exercise(
    val name: String,
    val durationSeconds: Int,
    val description: String,
    val animationRes: Int? = null
)

data class WorkoutActivity(
    val title: String,
    val exercises: List<Exercise>
)

object WorkoutDatabase {
    val allActivities = mapOf(
        "Haut du Corps Sculpté" to WorkoutActivity(
            title = "Haut du Corps Sculpté",
            exercises = listOf(
                Exercise("Pompes de l'Ombre", 45, "Gardez le corps bien aligné.", R.drawable.sculpt),
                Exercise("Dips de Rang A", 30, "Descendez jusqu'à 90 degrés.", R.drawable.sculpt),
                Exercise("Shadow Boxing", 60, "Vitesse et précision.", R.drawable.speed2)
            )
        ),
        "Puissance Bas du Corps" to WorkoutActivity(
            title = "Puissance Bas du Corps",
            exercises = listOf(
                Exercise("Squats de Monarque", 50, "Poids sur les talons.", R.drawable.bas),
                Exercise("Fentes de Chasseur", 40, "Gardez l'équilibre.", R.drawable.bas),
                Exercise("Chaise Invisible", 30, "Ne bougez plus, Chasseur.", R.drawable.bas)
            )
        ),
        "Gainage & Force Abdominale" to WorkoutActivity(
            title = "Gainage & Force Abdominale",
            exercises = listOf(
                Exercise("Planche de Mana", 60, "Contractez fort.", R.drawable.gen),
                Exercise("Russian Twist", 45, "Touchez chaque côté.", R.drawable.gen),
                Exercise("Crunchs de l'Ombre", 30, "Expirez à la montée.", R.drawable.gen)
            )
        ),
        "Éveil du Monarque" to WorkoutActivity(
            title = "Éveil du Monarque",
            exercises = listOf(
                Exercise("Burpees Explosifs", 45, "Donnez tout !", R.drawable.burn),
                Exercise("Sprints sur Place", 30, "Plus vite !", R.drawable.speed2),
                Exercise("Planche Commando", 60, "Force et stabilité.", R.drawable.gen)
            )
        ),
        "Brûle-Graisses Express" to WorkoutActivity(
            title = "Brûle-Graisses Express",
            exercises = listOf(
                Exercise("Jumping Jacks", 45, "Rythme constant.", R.drawable.burn),
                Exercise("Mountain Climbers", 30, "Genoux vers la poitrine.", R.drawable.burn)
            )
        ),
        "Mobilité & Flexibilité" to WorkoutActivity(
            title = "Mobilité & Flexibilité",
            exercises = listOf(
                Exercise("Rotation des Hanches", 60, "Mouvements amples.", R.drawable.mobility),
                Exercise("Étirement des Épaules", 45, "Tenez la position.", R.drawable.mobility)
            )
        )
        // Les autres programmes peuvent être ajoutés suivant ce modèle
    )
}
