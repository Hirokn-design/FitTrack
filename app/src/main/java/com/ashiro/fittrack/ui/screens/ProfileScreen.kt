package com.ashiro.fittrack.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.ui.components.SystemCard
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream

// Fonction utilitaire pour copier l'image sélectionnée localement de manière persistante
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_picture_hunter.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE) }

    // États mutables synchronisés avec le Système
    var username by remember { mutableStateOf(prefs.getString("USERNAME", "CHASSEUR INCONNU") ?: "CHASSEUR INCONNU") }
    var profilePicturePath by remember { mutableStateOf(prefs.getString("PROFILE_PICTURE_URI", null)) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newNameText by remember { mutableStateOf(username) }

    // Clé du Système : Permet de savoir si l'utilisateur a déjà modifié son profil une fois
    var isProfileLocked by remember { mutableStateOf(prefs.getBoolean("PROFILE_LOCKED", false)) }

    // 🔄 Versionneur d'état de l'avatar pour court-circuiter le cache de Coil et forcer la recomposition
    var avatarVersion by remember { mutableStateOf(0) }

    val rank = "RANG E" // Rang initial immuable au départ

    if (showEditDialog && !isProfileLocked) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = MaterialTheme.colorScheme.surface, // Force un fond de dialogue pur et contrasté
            title = {
                Text(
                    text = "CHANGEMENT DE L'IDENTIFIANT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurface // Visibilité maximale
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Attention : Le Système ne valide qu'une seule modification d'identité. Ce choix sera définitif.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedTextField(
                        value = newNameText,
                        onValueChange = { newNameText = it },
                        singleLine = true,
                        label = { Text("Nom du Chasseur") },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newNameText.isNotBlank()) {
                            // Enregistrement des données et verrouillage définitif dans le Système
                            prefs.edit()
                                .putString("USERNAME", newNameText.trim())
                                .putBoolean("PROFILE_LOCKED", true)
                                .apply()

                            username = newNameText.trim()
                            isProfileLocked = true
                            showEditDialog = false
                        }
                    }
                ) {
                    Text(
                        text = "CONFIRMER LE CHOIX",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(
                        text = "ANNULER",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER : STATISTIQUES DU CHASSEUR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "STATISTIQUES UTILISATEUR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .width(150.dp)
                                .padding(top = 4.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // BLOC IDENTITÉ (Avatar interactif + Données de Chasseur)
            item {
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        val savedPath = saveImageToInternalStorage(context, it)
                        if (savedPath != null) {
                            prefs.edit().putString("PROFILE_PICTURE_URI", savedPath).apply()
                            profilePicturePath = savedPath
                            avatarVersion++ // 🔄 Incrémentation de la version pour forcer la recomposition instantanée
                        }
                    }
                }

                SystemCard {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                        // Zone d'Avatar cliquable
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { galleryLauncher.launch("image/*") }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            )

                            if (profilePicturePath != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(File(profilePicturePath!!))
                                            // 🔄 Version Coil 2 : On change la clé du cache mémoire pour forcer le rafraîchissement
                                            .memoryCacheKey("profile_picture_$avatarVersion")
                                            .build()
                                    ),
                                    contentDescription = "Photo de profil",
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Ajouter une photo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = username.uppercase(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false)
                                )

                                // L'icône d'édition ne s'affiche QUE si le profil n'est pas encore verrouillé
                                if (!isProfileLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Éditer l'identifiant",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable {
                                                newNameText = username
                                                showEditDialog = true
                                            }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Identifiant Scellé",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Text(
                                text = "RANG : $rank",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            ProfileSmallStat(Icons.Default.Height, "${prefs.getInt("HEIGHT", 0)} CM")
                            ProfileSmallStat(Icons.Default.MonitorWeight, "${prefs.getInt("WEIGHT", 0)} KG")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Jauge d'XP
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "EXP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "75%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }

            // REGISTRE DES CAPACITÉS
            item {
                SystemCard {
                    Text(
                        text = "REGISTRE DES CAPACITÉS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SkillItem("FORCE", 0.60f)
                    SkillItem("AGILITÉ", 0.50f)
                    SkillItem("ENDURANCE", 0.40f)
                    SkillItem("INTELLIGENCE", 0.50f)
                }
            }

            // HISTORIQUE D'ACTIVITÉ
            item {
                SystemCard {
                    Text(
                        text = "HISTORIQUE D'ACTIVITÉ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, 0.7f).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .fillMaxHeight(h)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSmallStat(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SkillItem(label: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    }
}