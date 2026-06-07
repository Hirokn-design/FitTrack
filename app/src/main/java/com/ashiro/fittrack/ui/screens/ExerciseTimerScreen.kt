package com.ashiro.fittrack.ui.screens

import android.widget.Toast
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.model.WorkoutDatabase
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.theme.CyanElectric
import kotlinx.coroutines.delay

@Composable
fun ExerciseTimerScreen(
    activityName: String,
    onFinish: () -> Unit
) {
    val context = LocalContext.current

    // 1. Récupération sécurisée de l'activité
    val activity = remember(activityName) { WorkoutDatabase.allActivities[activityName] }

    // Si l'activité n'est pas trouvée, on affiche une alerte visuelle au lieu de laisser un écran noir infini
    if (activity == null) {
        LaunchedEffect(activityName) {
            Toast.makeText(context, "Erreur Raid : '$activityName' introuvable !", Toast.LENGTH_LONG).show()
            onFinish()
        }
        return
    }

    var currentExerciseIndex by remember { mutableIntStateOf(0) }

    // Sécurité anti-crash si la liste d'exercices est vide
    if (activity.exercises.isEmpty() || currentExerciseIndex >= activity.exercises.size) {
        LaunchedEffect(Unit) { onFinish() }
        return
    }

    val currentExercise = activity.exercises[currentExerciseIndex]

    // 2. Gestion du temps liée à l'index de l'exercice en cours
    var timeLeft by remember(currentExerciseIndex) { mutableIntStateOf(currentExercise.durationSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    // 3. Boucle d'effet unique et isolée pour le compte à rebours
    LaunchedEffect(currentExerciseIndex, isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--

            if (timeLeft <= 0) {
                if (currentExerciseIndex < activity.exercises.size - 1) {
                    currentExerciseIndex++
                } else {
                    onFinish()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RAID EN COURS",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanElectric
                )
                Text(
                    text = activity.title.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onFinish) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ZONE D'ANIMATION DE L'EXERCICE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .border(1.dp, CyanElectric.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (currentExercise.animationRes != null) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = currentExercise.animationRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = CyanElectric.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
            }

            Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomStart) {
                Text(
                    text = "EXERCICE ${currentExerciseIndex + 1}/${activity.exercises.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = currentExercise.name.uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // TIMER CIRCULAIRE
        Box(contentAlignment = Alignment.Center) {
            val totalSeconds = if (currentExercise.durationSeconds > 0) currentExercise.durationSeconds.toFloat() else 1f
            val progress = timeLeft.toFloat() / totalSeconds

            Canvas(modifier = Modifier.size(220.dp)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    style = Stroke(width = 8.dp.toPx())
                )
                drawArc(
                    color = CyanElectric,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "SECONDES",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanElectric
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // CONTROLES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SystemButton(
                text = if (isRunning) "PAUSE" else "REPRENDRE",
                onClick = { isRunning = !isRunning },
                modifier = Modifier.weight(1f),
                isSecondary = true
            )

            if (currentExerciseIndex < activity.exercises.size - 1) {
                SystemButton(
                    text = "SUIVANT",
                    onClick = { currentExerciseIndex++ },
                    modifier = Modifier.weight(1f)
                )
            } else {
                SystemButton(
                    text = "TERMINER",
                    onClick = onFinish,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}