package com.ashiro.fittrack.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashiro.fittrack.R
import com.ashiro.fittrack.ui.theme.*

/**
 * Extension Modifier pour appliquer l'effet de verre adaptatif (style iPhone en mode clair)
 */
@Composable
fun Modifier.glassEffect(
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    borderAlpha: Float = 0.6f
): Modifier {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) CardBackground.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.45f)
    val borderColor = if (isDark) CyanElectric.copy(alpha = 0.3f) else Color.White.copy(alpha = borderAlpha)
    
    return this
        .clip(shape)
        .background(bgColor)
        .border(1.dp, borderColor, shape)
}

@Composable
fun SystemCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .glassEffect()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
fun TrainingSessionCard(
    title: String,
    duration: String,
    exercisesCount: Int,
    calories: Int,
    muscles: String = "Global",
    instructions: List<String> = emptyList(),
    imageRes: Int,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var isExpanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassEffect(RoundedCornerShape(24.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Illustration style iPhone
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            fontSize = 15.sp
                        ),
                        color = if (isDark) Color.White else LightTextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // GRILLE DE STATISTIQUES MINIMALISTE (Style Tableau)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatIconItem(Icons.Default.Timer, duration, isDark) // Icons.Default.Timer
                        StatIconItem(Icons.Default.Accessibility, muscles, isDark) // Icons.Default.FitnessCenter
                        StatIconItem(Icons.Default.EnergySavingsLeaf, "${calories}kcal", isDark) // Icons.Default.Whatshot
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isDark) CyanElectric.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // ZONE EXTENSIBLE
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = (if (isDark) CyanElectric else LightPrimary).copy(alpha = 0.15f)
                    )
                    
                    if (instructions.isNotEmpty()) {
                        Text(
                            text = "PROTOCOLE DE MISSION :",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) CyanElectric else LightPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        instructions.forEach { step ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text("•", color = if (isDark) CyanElectric else LightPrimary, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    SystemButton(
                        text = "COMMENCER LE RAID",
                        onClick = onStart,
                        modifier = Modifier.height(46.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatIconItem(icon: ImageVector, text: String, isDark: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, 
            contentDescription = null, 
            modifier = Modifier.size(13.dp),
            tint = if (isDark) CyanElectric.copy(alpha = 0.8f) else LightPrimary.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isDark) Color.LightGray else Color.DarkGray
        )
    }
}

@Composable
fun SystemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isSecondary) Color.Transparent else (if (isDark) ManaPurple else LightPrimary)
    val contentColor = if (isSecondary) (if (isDark) CyanElectric else LightPrimary) else Color.White

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isSecondary) androidx.compose.foundation.BorderStroke(1.dp, if (isDark) CyanElectric else LightPrimary) else null
    ) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))
    }
}
