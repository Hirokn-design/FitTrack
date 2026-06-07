package com.ashiro.fittrack

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
                sharedPreferences.getString("THEME_MODE", "System")?.let { mutableStateOf(it) } ?: mutableStateOf("System")
            }

            val isDark = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            var customBgPath by remember {
                mutableStateOf(sharedPreferences.getString("CUSTOM_BG_PATH", null))
            }

            val backgroundPainter: Painter = if (customBgPath != null) {
                val bitmap = remember(customBgPath) {
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
                    }
                }
            }

            FitTrackTheme(darkTheme = isDark) {
                // Permissions
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

                // Sauvegarde de l'état encodé de manière sécurisée pour éviter les conflits d'accents/caractères spéciaux
                var activeExerciseEncoded by remember { mutableStateOf<String?>(null) }

                // Navigation d'écran plein écran
                var isProfileVisible by remember { mutableStateOf(false) }
                var isAboutVisible by remember { mutableStateOf(false) }
                var showSettingsDialog by remember { mutableStateOf(false) }
                var showMenu by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isSplashFinished) {
                        SplashScreen(onTimeout = { isSplashFinished = true })
                    } else {
                        // FOND UNIVERSEL DYNAMIQUE
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
                            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.35f), androidx.compose.ui.graphics.BlendMode.Darken)
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
                            // CORRECTION DE L'AFFICHAGE : Décodage propre avant injection dans le minuteur d'exercice
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
                                            title = { Text("INTERFACE SYSTEME", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp), color = CyanElectric) },
                                            actions = {
                                                Box {
                                                    IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White) }
                                                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(SystemBackground.copy(alpha = 0.95f))) {
                                                        DropdownMenuItem(text = { Text("MON PROFIL", color = Color.White) }, onClick = { showMenu = false; isProfileVisible = true }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanElectric) })
                                                        DropdownMenuItem(text = { Text("PARAMÈTRES", color = Color.White) }, onClick = { showMenu = false; showSettingsDialog = true }, leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = CyanElectric) })
                                                        DropdownMenuItem(text = { Text("À PROPOS", color = Color.White) }, onClick = { showMenu = false; isAboutVisible = true }, leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = CyanElectric) })
                                                    }
                                                }
                                            },
                                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black.copy(alpha = 0.4f))
                                        )
                                    },
                                    bottomBar = { SystemBottomNavigation(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) }
                                ) { innerPadding ->
                                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                                        when (selectedTab) {
                                            0 -> MainDashboard(savedName!!, _totalSteps.intValue)
                                            // CORRECTION DE L'ACTION : Encodage sécurisé en UTF-8 pour éliminer le bug d'affichage
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
                                    onThemeChange = { themeMode = it; sharedPreferences.edit { putString("THEME_MODE", it) } },
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
        confirmButton = { TextButton(onClick = onDismiss) { Text("FERMER", color = CyanElectric) } },
        containerColor = SystemBackground.copy(alpha = 0.95f),
        title = { Text("PARAMÈTRES", style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp), color = CyanElectric) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("AFFICHAGE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                listOf("System" to "Système", "Light" to "Clair", "Dark" to "Sombre").forEach { (mode, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = currentTheme == mode, onClick = { onThemeChange(mode) }, colors = RadioButtonDefaults.colors(selectedColor = CyanElectric))
                        Text(label, color = Color.White, modifier = Modifier.clickable { onThemeChange(mode) })
                    }
                }
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Text("ARRIÈRE-PLAN", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Button(onClick = onBackgroundChange, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CyanElectric.copy(alpha = 0.2f), contentColor = CyanElectric), shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("CHANGER L'IMAGE")
                }
                if (hasCustomBg) {
                    TextButton(onClick = onResetBackground, modifier = Modifier.fillMaxWidth()) { Text("RÉINITIALISER", color = Color.Red.copy(alpha = 0.7f), fontSize = 10.sp) }
                }
            }
        },
        modifier = Modifier.border(1.dp, CyanElectric.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
    )
}

@Composable
fun SystemBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf("Quête" to Icons.Default.CheckCircle, "Activiés" to Icons.AutoMirrored.Filled.List, "Évolution" to Icons.Default.Star, "Repas" to Icons.Default.Fastfood)
    val menuShape = RoundedCornerShape(32.dp)
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp).fillMaxWidth().clip(menuShape).background(SystemBackground.copy(alpha = 0.85f)).border(1.dp, CyanElectric.copy(alpha = 0.4f), menuShape)) {
        NavigationBar(containerColor = Color.Transparent, modifier = Modifier.height(72.dp), windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp), tonalElevation = 0.dp) {
            items.forEachIndexed { index, (label, icon) ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanElectric, selectedTextColor = CyanElectric, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = CyanElectric.copy(alpha = 0.2f))
                )
            }
        }
    }
}