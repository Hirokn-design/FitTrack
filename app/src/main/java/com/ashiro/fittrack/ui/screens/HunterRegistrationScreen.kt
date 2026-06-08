package com.ashiro.fittrack.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.ManaPurple
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun HunterRegistrationScreen(
    onComplete: (HunterProfile) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 7 })
    val scope = rememberCoroutineScope()

    // États du formulaire
    var name by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("M") }
    var age by remember { mutableIntStateOf(25) }
    var height by remember { mutableIntStateOf(175) }
    var weight by remember { mutableIntStateOf(70) }
    var goal by remember { mutableStateOf("Prendre du muscle") }
    var activityLevel by remember { mutableStateOf("Intermédiaire") }

    // ÉTAT D'INITIALISATION ET DE SYNCHRONISATION FINALE
    var isSyncing by remember { mutableStateOf(false) }

    // Chargement de l'animation Lottie pour la création du profil
    val syncComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.system_registration_sync)
    )
    val syncProgress by animateLottieCompositionAsState(
        composition = syncComposition,
        iterations = LottieConstants.IterateForever,
        speed = 1.2f
    )

    // Effet de temporisation de l'analyse du profil (4 secondes)
    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            delay(4000L) // Laisse le temps à l'animation et à la BDD de s'initialiser
            onComplete(HunterProfile(name, sex, age, height, weight, goal, activityLevel))
        }
    }

    // ÉCRAN DE SYNCHRONISATION DU SYSTÈME (S'affiche uniquement lors du clic final)
    if (isSyncing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0F19)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (syncComposition != null) {
                    LottieAnimation(
                        composition = syncComposition,
                        progress = { syncProgress },
                        modifier = Modifier.size(180.dp)
                    )
                } else {
                    CircularProgressIndicator(color = CyanElectric, modifier = Modifier.size(44.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "GÉNÉRATION DE L'INTERFACE...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = CyanElectric
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enregistrement des informations de l'utilisateur",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
        return // Coupe le rendu du formulaire d'inscription
    }

    // FORMULAIRE D'INSCRIPTION STANDARD (Horizontal Pager)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // BARRE DE SYNCHRONISATION
            Column(modifier = Modifier.padding(top = 60.dp, start = 40.dp, end = 40.dp)) {
                Text(
                    text = "INITIALISATION : ${(pagerState.currentPage + 1) * 100 / 7}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanElectric,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { (pagerState.currentPage + 1).toFloat() / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = CyanElectric,
                    trackColor = Color.White.copy(alpha = 0.1f),
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false,
                beyondViewportPageCount = 1
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(32.dp))
                            .border(1.dp, CyanElectric.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ASSISTANT AVATAR
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(ManaPurple.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, CyanElectric.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = "Assistant",
                                tint = CyanElectric,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        when (page) {
                            0 -> NameStep(name) { name = it }
                            1 -> SexStep(sex) { sex = it }
                            2 -> AgeStep(age) { age = it }
                            3 -> HeightStep(height) { height = it }
                            4 -> WeightStep(weight) { weight = it }
                            5 -> GoalStep(goal) { goal = it }
                            6 -> ActivityLevelStep(activityLevel) { activityLevel = it }
                        }
                    }
                }
            }

            // NAVIGATION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    SystemButton(
                        text = "RETOUR",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1,
                                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        isSecondary = true
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                SystemButton(
                    text = if (pagerState.currentPage == 6) "INITIALISER" else "SUIVANT",
                    onClick = {
                        if (pagerState.currentPage < 6) {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1,
                                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                                )
                            }
                        } else {
                            isSyncing = true // Déclenche le superbe écran d'analyse Lottie
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = if (pagerState.currentPage == 0) name.isNotBlank() else true
                )
            }
        }
    }
}

// Les sous-composants existants restent inchangés
@Composable
fun NameStep(name: String, onNameChange: (String) -> Unit) {
    StepLayout(title = "IDENTIFIANT", subtitle = "Enregistrez votre pseudo") {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            textStyle = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Center, fontSize = 20.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanElectric,
                unfocusedBorderColor = Color.Gray,
                cursorColor = CyanElectric
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun SexStep(selectedSex: String, onSexSelect: (String) -> Unit) {
    StepLayout(title = "PROFIL BIOLOGIQUE", subtitle = "Choisissez votre sexe") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SexCircleButton(
                label = "MÂLE",
                icon = Icons.Default.Male,
                isSelected = selectedSex == "M",
                onClick = { onSexSelect("M") }
            )
            Spacer(modifier = Modifier.height(32.dp))
            SexCircleButton(
                label = "FEMELLE",
                icon = Icons.Default.Female,
                isSelected = selectedSex == "F",
                onClick = { onSexSelect("F") }
            )
        }
    }
}

@Composable
fun AgeStep(selectedAge: Int, onAgeSelect: (Int) -> Unit) {
    StepLayout(title = "CYCLE DE VIE", subtitle = "Âge actuel") {
        WheelPicker(range = 10..100, selectedValue = selectedAge, onValueSelected = onAgeSelect, unit = "ANS")
    }
}

@Composable
fun HeightStep(selectedHeight: Int, onHeightSelect: (Int) -> Unit) {
    StepLayout(title = "ENVERGURE", subtitle = "Taille en centimètres") {
        WheelPicker(range = 100..250, selectedValue = selectedHeight, onValueSelected = onHeightSelect, unit = "CM")
    }
}

@Composable
fun WeightStep(selectedWeight: Int, onWeightSelect: (Int) -> Unit) {
    StepLayout(title = "MASSE", subtitle = "Poids en kilogrammes") {
        WheelPicker(range = 30..200, selectedValue = selectedWeight, onValueSelected = onWeightSelect, unit = "KG")
    }
}

@Composable
fun GoalStep(selectedGoal: String, onGoalSelect: (String) -> Unit) {
    val goals = listOf("Perdre du poids", "Prendre du muscle", "Endurance", "Remise en forme")
    StepLayout(title = "MISSION", subtitle = "Quel est votre objectif ?") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            goals.forEach { goal ->
                SelectionBox(label = goal, isSelected = selectedGoal == goal) { onGoalSelect(goal) }
            }
        }
    }
}

@Composable
fun ActivityLevelStep(selectedLevel: String, onLevelSelect: (String) -> Unit) {
    val levels = listOf("Débutant", "Intermédiaire", "Avancé", "Expert")
    StepLayout(title = "NIVEAU D'ÉNERGIE", subtitle = "Évaluez votre potentiel") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            levels.forEach { level ->
                SelectionBox(label = level, isSelected = selectedLevel == level) { onLevelSelect(level) }
            }
        }
    }
}

@Composable
fun StepLayout(title: String, subtitle: String, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp), color = Color.White)
        Text(text = subtitle.uppercase(), style = MaterialTheme.typography.labelSmall, color = CyanElectric, modifier = Modifier.padding(bottom = 24.dp))
        content()
    }
}

@Composable
fun SexCircleButton(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (isSelected) ManaPurple.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f))
                .border(2.dp, if (isSelected) CyanElectric else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(54.dp)
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) CyanElectric else Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SelectionBox(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ManaPurple.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (isSelected) CyanElectric else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = label.uppercase(), style = MaterialTheme.typography.labelMedium, color = if (isSelected) CyanElectric else Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
fun WheelPicker(
    range: IntRange,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    unit: String
) {
    val itemHeight = 60.dp
    val rangeList = range.toList()
    val initialPage = rangeList.indexOf(selectedValue).coerceAtLeast(0)

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { rangeList.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onValueSelected(rangeList[pagerState.currentPage])
    }

    Box(
        modifier = Modifier
            .height(itemHeight * 3)
            .width(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(itemHeight)
                .fillMaxWidth()
                .border(1.dp, CyanElectric.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(CyanElectric.copy(alpha = 0.05f))
        )

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight)
        ) { page ->
            val value = rangeList[page]
            val isSelected = page == pagerState.currentPage

            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = 1.2f - (pageOffset.coerceIn(0f, 1f) * 0.3f)
            val alpha = 1f - (pageOffset.coerceIn(0f, 1f) * 0.7f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value $unit",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = if (isSelected) CyanElectric else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class HunterProfile(
    val name: String,
    val sex: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val goal: String,
    val activityLevel: String
)