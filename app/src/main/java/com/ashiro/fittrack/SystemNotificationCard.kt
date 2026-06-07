package com.ashiro.fittrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ashiro.fittrack.ui.components.SystemButton
import com.ashiro.fittrack.ui.components.SystemCard
import com.ashiro.fittrack.ui.theme.CyanElectric
import com.ashiro.fittrack.ui.theme.FitTrackTheme

@Composable
fun SystemNotificationCard(
    onRegistrationComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }

    SystemCard(
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ALERTE DU SYSTÈME",
                style = MaterialTheme.typography.titleLarge,
                color = CyanElectric
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = CyanElectric.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "INITIALISATION DU PROGRAMME D'ENTRAÎNEMENT",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("IDENTIFIANT DU CHASSEUR", style = MaterialTheme.typography.labelMedium) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanElectric,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = CyanElectric,
                    cursorColor = CyanElectric
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            SystemButton(
                text = "S'ENREGISTRER",
                onClick = {
                    if (username.trim().isNotEmpty()) {
                        onRegistrationComplete(username)
                    }
                },
                enabled = username.trim().isNotEmpty()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun SystemNotificationCardPreview() {
    FitTrackTheme {
        SystemNotificationCard(onRegistrationComplete = {})
    }
}