package com.ashiro.fittrack.ui.screens

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
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
//import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.model.WorkoutDatabase
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.ManaPurple
import kotlinx.coroutines.delay

@Composable
fun ExerciseTimerScreen(
    activityName: String,
    onFinish: () -> Unit
) {
    val activity = WorkoutDatabase.allActivities[activityName] ?: return
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    val currentExercise = activity.exercises[currentExerciseIndex]
    
    var timeLeft by remember { mutableIntStateOf(currentExercise.durationSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    // Reset timer when exercise changes
    LaunchedEffect(currentExerciseIndex) {
        timeLeft = activity.exercises[currentExerciseIndex].durationSeconds
        isRunning = true
    }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
            if (timeLeft == 0) {
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
                // Placeholder pour l'animation (Image pour l'instant)
                androidx.compose.foundation.Image(
                    painter = painterResource(id = currentExercise.animationRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = CyanElectric.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
            }
            
            // Overlay Text
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
            val progress = timeLeft.toFloat() / currentExercise.durationSeconds.toFloat()
            
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
                    text = "${(timeLeft / 60).toString().padStart(2, '0')}:${(timeLeft % 60).toString().padStart(2, '0')}",
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
