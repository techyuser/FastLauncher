package com.fastlauncher.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fastlauncher.app.data.AppInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onOpenApp: (android.content.Intent) -> Unit,
    onLaunchAppIntent: (String, String) -> android.content.Intent?,
    onOpenDrawer: () -> Unit
) {
    var currentTime by remember { mutableStateOf(getFormattedTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10000)
            currentTime = getFormattedTime()
        }
    }

    // OxygenOS / ColorOS Signature Gradient Background
    val oxygenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E1B4B), // Deep indigo
            Color(0xFF0F172A), // Slate dark
            Color(0xFF090D16)  // Near black
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(oxygenGradient)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // OnePlus Style Weather & Clock Widget Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = currentTime,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = getFormattedDate(),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Weather",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "24°C · Sunny",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // OxygenOS / ColorOS "Global Search" Pill Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable { onOpenDrawer() },
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Global Search",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Global Search (Apps, Files, Web)...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Frequently used apps grid (Home Screen Icons)
            val homeApps = apps.take(16)

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(homeApps, key = { it.packageName + it.activityName }) { app ->
                    AppIconItem(
                        app = app,
                        onClick = {
                            onLaunchAppIntent(app.packageName, app.activityName)?.let { intent ->
                                onOpenApp(intent)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Dock / Drawer Trigger Pill
            Button(
                onClick = onOpenDrawer,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = "Swipe up for App Drawer & Categories",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AppIconItem(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        val bitmap = remember(app.icon) {
            app.icon?.toBitmap(width = 128, height = 128)?.asImageBitmap()
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = app.label.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

private fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return sdf.format(Date())
}
