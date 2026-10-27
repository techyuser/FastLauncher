package com.fastlauncher.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fastlauncher.app.data.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScreen(
    isOpen: Boolean,
    apps: List<AppInfo>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    onOpenApp: (android.content.Intent) -> Unit,
    onLaunchAppIntent: (String, String) -> android.content.Intent?
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE090D16))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top bar with search input & close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Search ${apps.size} apps...", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                            .size(56.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val filteredApps = remember(apps, searchQuery) {
                    if (searchQuery.isBlank()) apps
                    else apps.filter { it.label.contains(searchQuery, ignoreCase = true) }
                }

                // App Drawer Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredApps, key = { it.packageName + it.activityName }) { app ->
                        AppIconItem(
                            app = app,
                            onClick = {
                                onLaunchAppIntent(app.packageName, app.activityName)?.let { intent ->
                                    onOpenApp(intent)
                                    onCloseDrawer()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
