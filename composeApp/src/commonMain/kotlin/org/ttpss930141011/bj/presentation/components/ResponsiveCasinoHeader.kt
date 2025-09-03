package org.ttpss930141011.bj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.presentation.responsive.ResponsiveLayout
import org.ttpss930141011.bj.presentation.responsive.WindowInfo
import org.ttpss930141011.bj.presentation.responsive.getResponsivePadding
import org.ttpss930141011.bj.presentation.responsive.getCardCornerRadius

@Composable
fun ResponsiveCasinoHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit
) {
    ResponsiveLayout { windowInfo ->
        when {
            windowInfo.isCompact -> CompactHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
            windowInfo.isMedium -> MediumHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
            else -> ExpandedHeader(
                balance = balance,
                onShowSettings = onShowSettings,
                hasStats = hasStats,
                onShowSummary = onShowSummary,
                windowInfo = windowInfo
            )
        }
    }
}

@Composable
private fun CompactHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    windowInfo: WindowInfo
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getResponsivePadding())
    ) {
        // First row: Title and Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BJ Strategy Trainer",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Master basic strategy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5D6A7),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasStats) {
                    CompactIconButton(
                        icon = "📊",
                        onClick = onShowSummary,
                        color = Color(0xFF4CAF50)
                    )
                }
                CompactIconButton(
                    icon = "⚙️",
                    onClick = onShowSettings,
                    color = Color.White.copy(alpha = 0.2f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Second row: Balance badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFC107)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance: ",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$$balance",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MediumHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    windowInfo: WindowInfo
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getResponsivePadding()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Blackjack Strategy Trainer",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Master optimal basic strategy",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFA5D6A7),
                fontSize = 13.sp
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFC107)
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Balance:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$$balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            if (hasStats) {
                MediumIconButton(
                    icon = "📊",
                    onClick = onShowSummary,
                    color = Color(0xFF4CAF50)
                )
            }
            
            MediumIconButton(
                icon = "⚙️",
                onClick = onShowSettings,
                color = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun ExpandedHeader(
    balance: Int,
    onShowSettings: () -> Unit,
    hasStats: Boolean,
    onShowSummary: () -> Unit,
    windowInfo: WindowInfo
) {
    // Use the original desktop header for expanded screens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = windowInfo.getResponsivePadding()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Blackjack Strategy Trainer",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "Master optimal basic strategy",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFA5D6A7),
                fontSize = 14.sp
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFC107)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Balance:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$$balance",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            
            if (hasStats) {
                ExpandedIconButton(
                    icon = "📊",
                    onClick = onShowSummary,
                    color = Color(0xFF4CAF50)
                )
            }
            
            ExpandedIconButton(
                icon = "⚙️",
                onClick = onShowSettings,
                color = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun CompactIconButton(
    icon: String,
    onClick: () -> Unit,
    color: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White
        )
    }
}

@Composable
private fun MediumIconButton(
    icon: String,
    onClick: () -> Unit,
    color: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape
            )
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
private fun ExpandedIconButton(
    icon: String,
    onClick: () -> Unit,
    color: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color, color.copy(alpha = 0.8f))
                )
            )
            .shadow(
                elevation = 6.dp,
                shape = CircleShape
            )
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}