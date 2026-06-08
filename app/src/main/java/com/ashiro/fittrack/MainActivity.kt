package com.ashiro.fittrack

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.core.tween
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.ashiro.fittrack.ui.screens.*
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.FitTrackTheme
import com.ashiro.fittrack.ui.theme.SystemBackground
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var _totalSteps = mutableIntStateOf(0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
        _totalSteps.intValue = sharedPreferences.getInt("DAILY_STEPS", 0)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        setContent {
            var themeMode by remember {
                mutableStateOf(sharedPreferences.getString("THEME_MODE", "System") ?: "System")
            }

            // Gestion dynamique du mode d'affichage selon les préférences ou l'état système Android
            val isDark = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            var customBgPath by remember {
                mutableStateOf(sharedPreferences.getString("CUSTOM_BG_PATH", null))
            }

            // 🔄 Versionneur d'état du fond d'écran pour forcer le rafraîchissement de BitmapFactory
            var bgVersion by remember { mutableStateOf(0) }

            // Re-calculer le painter uniquement lorsque le chemin OU la version change
            val backgroundPainter: Painter = if (customBgPath != null) {
                val bitmap = remember(customBgPath, bgVersion) { // 🔥 Observe la version ici
                    try {
                        val file = File(customBgPath!!)
                        if (file.exists()) {
                            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                        } else null
                    } catch (e: Exception) { null }
                }
                if (bitmap != null) BitmapPainter(bitmap) else painterResource(id = R.drawable.bg_init)
            } else {
                painterResource(id = R.drawable.bg_hom)
            }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    val path = saveImageToInternalStorage(uri, "custom_background.jpg")
                    if (path != null) {
                        customBgPath = path
                        sharedPreferences.edit { putString("CUSTOM_BG_PATH", path) }
                        bgVersion++ // 🔄 Force le bloc "remember" ci-dessus à recalculer le Bitmap avec le nouveau fichier
                    }
                }
            }

            // Application globale de ton thème mis à jour
            FitTrackTheme(darkTheme = isDark) {
                // Gestion des permissions Système (Podomètre et Notifications)
                val permissionsToRequest = remember {
                    val list = mutableListOf<String>()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) list.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) list.add(Manifest.permission.POST_NOTIFICATIONS)
                    list
                }
                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }
                LaunchedEffect(Unit) { if (permissionsToRequest.isNotEmpty()) permissionLauncher.launch(permissionsToRequest.toTypedArray()) }

                var isSplashFinished by remember { mutableStateOf(false) }
                var isOnboardingFinished by remember {
                    mutableStateOf(sharedPreferences.getBoolean("ONBOARDING_FINISHED", false))
                }
                var savedName by remember { mutableStateOf(sharedPreferences.getString("USERNAME", null)) }
                var savedSex by remember { mutableStateOf(sharedPreferences.getString("SEX", "M")) }
                var selectedTab by remember { mutableIntStateOf(0) }

                // Sauvegarde de l'état encodé pour la navigation vers l'exercice
                var activeExerciseEncoded by remember { mutableStateOf<String?>(null) }

                // Gestion des fenêtres d'interfaces superposées
                var isProfileVisible by remember { mutableStateOf(false) }
                var isAboutVisible by remember { mutableStateOf(false) }
                var showSettingsDialog by remember { mutableStateOf(false) }
                var showMenu by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isSplashFinished) {
                        SplashScreen(onTimeout = { isSplashFinished = true })
                    } else {
                        // GESTION ET ADAPTATION DU FOND UNIVERSEL DYNAMIQUE
                        val currentBg: Painter = if (customBgPath != null) backgroundPainter else {
                            val resId = when {
                                savedName == null -> R.drawable.bg_init
                                savedSex == "M" -> R.drawable.bg_hom
                                else -> R.drawable.bg_fem
                            }
                            painterResource(id = resId)
                        }

                        Image(
                            painter = currentBg,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(
                                color = if (isDark) Color.Black.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.15f),
                                blendMode = if (isDark) androidx.compose.ui.graphics.BlendMode.Darken else androidx.compose.ui.graphics.BlendMode.Lighten
                            )
                        )

                        when {
                            !isOnboardingFinished -> {
                                OnboardingScreen(onFinished = {
                                    sharedPreferences.edit { putBoolean("ONBOARDING_FINISHED", true) }
                                    isOnboardingFinished = true
                                })
                            }
                            isAboutVisible -> {
                                AboutScreen(onBack = { isAboutVisible = false })
                            }
                            isProfileVisible -> {
                                ProfileScreen(onBack = { isProfileVisible = false })
                            }
                            savedName == null -> {
                                // Formulaire d'inscription couplé à notre animation finale de synchronisation
                                HunterRegistrationScreen(onComplete = { profile ->
                                    sharedPreferences.edit {
                                        putString("USERNAME", profile.name)
                                        putString("SEX", profile.sex)
                                        putInt("AGE", profile.age)
                                        putInt("HEIGHT", profile.height)
                                        putInt("WEIGHT", profile.weight)
                                        putString("GOAL", profile.goal)
                                        putString("ACTIVITY_LEVEL", profile.activityLevel)
                                    }
                                    savedSex = profile.sex
                                    savedName = profile.name
                                })
                            }
                            // Décodage et lancement du minuteur d'entraînement
                            activeExerciseEncoded != null -> {
                                val decodedActivityName = remember(activeExerciseEncoded) {
                                    try {
                                        URLDecoder.decode(activeExerciseEncoded!!, "UTF-8")
                                    } catch (e: Exception) { "" }
                                }

                                ExerciseTimerScreen(
                                    activityName = decodedActivityName,
                                    onFinish = { activeExerciseEncoded = null }
                                )
                            }
                            else -> {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    containerColor = Color.Transparent,
                                    topBar = {
                                        CenterAlignedTopAppBar(
                                            title = {
                                                Text(
                                                    text = "INTERFACE SYSTÈME",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        letterSpacing = 3.sp,
                                                        fontStyle = FontStyle.Normal
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            },
                                            actions = {
                                                Box {
                                                    IconButton(onClick = { showMenu = true }) {
                                                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onBackground)
                                                    }
                                                    DropdownMenu(
                                                        expanded = showMenu,
                                                        onDismissRequest = { showMenu = false },
                                                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                                    ) {
                                                        DropdownMenuItem(
                                                            text = { Text("MON PROFIL", color = MaterialTheme.colorScheme.onSurface) },
                                                            onClick = { showMenu = false; isProfileVisible = true },
                                                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                                        )
                                                        DropdownMenuItem(
                                                            text = { Text("PARAMÈTRES", color = MaterialTheme.colorScheme.onSurface) },
                                                            onClick = { showMenu = false; showSettingsDialog = true },
                                                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                                        )
                                                        DropdownMenuItem(
                                                            text = { Text("À PROPOS", color = MaterialTheme.colorScheme.onSurface) },
                                                            onClick = { showMenu = false; isAboutVisible = true },
                                                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                                        )
                                                    }
                                                }
                                            },
                                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                                            )
                                        )
                                    },
                                    bottomBar = { SystemBottomNavigation(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) }
                                ) { innerPadding ->
                                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                                        when (selectedTab) {
                                            0 -> MainDashboard(savedName!!, _totalSteps.intValue, onStartTraining = { rawTitle ->
                                                activeExerciseEncoded = try {
                                                    URLEncoder.encode(rawTitle, "UTF-8")
                                                } catch (e: Exception) { rawTitle }
                                            })
                                            1 -> TrainingProgramsScreen(onStartTraining = { rawTitle ->
                                                activeExerciseEncoded = try {
                                                    URLEncoder.encode(rawTitle, "UTF-8")
                                                } catch (e: Exception) { rawTitle }
                                            })
                                            2 -> StatsScreen()
                                            3 -> NutritionScreen()
                                        }
                                    }
                                }
                                if (showSettingsDialog) SettingsDialog(
                                    currentTheme = themeMode,
                                    onThemeChange = { mode ->
                                        themeMode = mode
                                        sharedPreferences.edit { putString("THEME_MODE", mode) }
                                    },
                                    onBackgroundChange = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    onResetBackground = { customBgPath = null; sharedPreferences.edit { remove("CUSTOM_BG_PATH") } },
                                    hasCustomBg = customBgPath != null,
                                    onDismiss = { showSettingsDialog = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, fileName: String): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) { null }
    }

    override fun onResume() {
        super.onResume()
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) else PackageManager.PERMISSION_GRANTED
        if (permission == PackageManager.PERMISSION_GRANTED) stepSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }
    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE).edit { putInt("DAILY_STEPS", _totalSteps.intValue) }
    }
    override fun onSensorChanged(event: SensorEvent?) { if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) _totalSteps.intValue += 1 }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(currentTheme: String, onThemeChange: (String) -> Unit, onBackgroundChange: () -> Unit, onResetBackground: () -> Unit, hasCustomBg: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("FERMER", color = MaterialTheme.colorScheme.primary) } },
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        title = { Text("PARAMÈTRES", style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp), color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("AFFICHAGE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                listOf("System" to "Système", "Light" to "Clair", "Dark" to "Sombre").forEach { (mode, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentTheme == mode,
                            onClick = { onThemeChange(mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(label, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable { onThemeChange(mode) })
                    }
                }
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Text("ARRIÈRE-PLAN", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Button(
                    onClick = onBackgroundChange,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("CHANGER L'IMAGE")
                }
                if (hasCustomBg) {
                    TextButton(onClick = onResetBackground, modifier = Modifier.fillMaxWidth()) {
                        Text("RÉINITIALISER", color = Color.Red.copy(alpha = 0.7f), fontSize = 10.sp)
                    }
                }
            }
        },
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
    )
}

@Composable
fun SystemBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val menuShape = RoundedCornerShape(32.dp)

    val navigationItems = listOf(
        Triple("Quête", Icons.Default.CheckCircle, 0),
        Triple("Activités", Icons.AutoMirrored.Filled.List, 1),
        Triple("Évolution", Icons.Default.Star, 2),
        Triple("Repas", Icons.Default.Fastfood, 3)
    )

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .fillMaxWidth()
            .height(72.dp)
            .clip(menuShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), menuShape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEach { (label, icon, index) ->
                val isSelected = selectedTab == index

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    animationSpec = tween(durationMillis = 300)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = contentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 1.sp
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}
