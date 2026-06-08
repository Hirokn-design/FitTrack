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
                Exercise("Push-ups", 45, "Gardez le corps bien aligné.",R.raw.ex_pompes),
                Exercise("Dips en appui", 30, "Descendez jusqu'à 90 degrés.", R.raw.ex_dips),
                Exercise("Jump Rope", 60, "Corde à sauter simulée.", R.raw.ex_jump_rope)
            )
        ),
        "Puissance Bas du Corps" to WorkoutActivity(
            title = "Puissance Bas du Corps",
            exercises = listOf(
                Exercise("Squats", 50, "Poids sur les talons.", R.raw.ex_squats),
                Exercise("Fentes", 40, "Gardez l'équilibre.", R.raw.ex_fentes),
                Exercise("Chaise Invisible", 30, "Ne bougez plus.", R.raw.ex_chaise)
            )
        ),
        "Gainage & Force Abdominale" to WorkoutActivity(
            title = "Gainage & Force Abdominale",
            exercises = listOf(
                Exercise("Planche rotative", 60, "Contractez fort.", R.raw.ex_planche),
                Exercise("Twist russe", 45, "Touchez chaque côté.", R.raw.ex_twist),
                Exercise("Crunchs abdominaux", 30, "Expirez à la montée.", R.raw.ex_crunchs)
            )
        ),
        "HIIT Explosif" to WorkoutActivity(
            title = "HIIT Explosif",
            exercises = listOf(
                Exercise("Burpees", 45, "Vitesse maximale.", R.raw.ex_burpees),
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Étirement latéral", 45, "Explosivité au sol.", R.raw.ex_etir)
            )
        ),
        "Brûle-Graisses Express" to WorkoutActivity(
            title = "Brûle-Graisses Express",
            exercises = listOf(
                Exercise("Corde à Sauter", 60, "Légèreté et vitesse.", R.raw.ex_jump_rope),
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Twist russe", 45, "Touchez chaque côté.", R.raw.ex_twist)
                )
        ),
        "Réveil Musculaire Doux" to WorkoutActivity(
            title = "Réveil Musculaire Doux",
            exercises = listOf(
                Exercise("Squats", 50, "Poids sur les talons.", R.raw.ex_squats),
                Exercise("Ouverture de hanche en marchant", 30, "Relâchez la tension.", R.raw.ex_walk),
                Exercise("Split jump", 60, "Gardez le corps bien aligné.", R.raw.ex_split_jump)

            )
        ),
        "Mobilité & Flexibilité" to WorkoutActivity(
            title = "Mobilité & Flexibilité",
            exercises = listOf(
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Split jump", 60, "Gardez le corps bien aligné.", R.raw.ex_split_jump),
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Ouverture de hanche en marchant", 30, "Relâchez la tension.", R.raw.ex_walk)
                )
        ),
        "Stretching Profond" to WorkoutActivity(
            title = "Stretching Profond",
            exercises = listOf(
                Exercise("Étirement Ischios", 45, "Ne forcez pas trop.", R.raw.ex_etir_),
                Exercise("Flexion Avant", 45, "Relâchez le dos.", R.raw.ex_flex)
            )
        ),
        "Agilité & Vitesse" to WorkoutActivity(
            title = "Agilité & Vitesse",
            exercises = listOf(
                Exercise("Corde à Sauter", 60, "Légèreté et vitesse.", R.raw.ex_jump_rope),
                Exercise("Twist russe", 45, "Touchez chaque côté.", R.raw.ex_twist),
                Exercise("Petit Saut", 60, "Prenez votre temps.", R.raw.ex_little_jump),
                Exercise("Course sur place", 60, "Vitesse et équilibre.", R.raw.ex_run)

            )
        ),
        "Éveil du Monarque" to WorkoutActivity(
            title = "Éveil du Monarque",
            exercises = listOf(
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Split jump", 60, "Gardez le corps bien aligné.", R.raw.ex_split_jump),
                Exercise("Jumping Jacks", 30, "Rythme constant.", R.raw.ex_jumping_jack),
                Exercise("Ouverture de hanche en marchant", 30, "Relâchez la tension.", R.raw.ex_walk),
                Exercise("Étirement Ischios", 45, "Ne forcez pas trop.", R.raw.ex_etir_),
                Exercise("Course sur place", 60, "Vitesse et équilibre.", R.raw.ex_run),
                Exercise("Twist russe", 45, "Touchez chaque côté.", R.raw.ex_twist),
                Exercise("Shadow Boxing", 60, "Comme si vous combattiez.", R.raw.ex_box),
                Exercise("Planche Commando", 45, "Force et stabilité.", R.raw.ex_planche_com),
                Exercise("Burpees", 45, "L'ultime test.", R.raw.ex_burpees),
                Exercise("Poids", 15, "Jusqu'à où voulez vous aller.", R.raw.ex_alt)

            )
        )
    )
}
