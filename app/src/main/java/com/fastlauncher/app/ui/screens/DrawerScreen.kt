package com.fastlauncher.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Categories
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        val drawerGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F172A),
                Color(0xFF050B14)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(drawerGradient)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Global Search Bar & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Global Search (${apps.size} apps)...", color = Color.White.copy(alpha = 0.5f)) },
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
                            focusedContainerColor = Color.White.copy(alpha = 0.12f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                            .size(52.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OxygenOS Style Category Tabs ("All" vs "Categories")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TabChip(
                        title = "All",
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            selectedCategoryFilter = null
                        }
                    )
                    TabChip(
                        title = "Categories",
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                        }
                    )
                }

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Category Sub-filters
                    val categories = listOf("Social", "Tools", "Media", "Games", "Google")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            SubCategoryChip(
                                title = cat,
                                selected = selectedCategoryFilter == cat,
                                onClick = {
                                    selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val filteredApps = remember(apps, searchQuery, selectedCategoryFilter) {
                    var list = apps
                    if (searchQuery.isNotBlank()) {
                        list = list.filter { it.label.contains(searchQuery, ignoreCase = true) }
                    }
                    if (selectedCategoryFilter != null) {
                        // Simple heuristic categorization for demo
                        list = list.filter { it.label.contains(selectedCategoryFilter!!, ignoreCase = true) || it.packageName.contains(selectedCategoryFilter!!.lowercase()) }
                    }
                    list
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // App Drawer Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // OxygenOS Style A-Z Alphabetical Index Sidebar
                    Column(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight()
                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val alphabet = ('A'..'Z').toList()
                        // Show subset of alphabet to fit nicely
                        val sampleAlpha = listOf('A', 'C', 'D', 'G', 'M', 'P', 'S', 'W', 'Y', '#')
                        sampleAlpha.forEach { char ->
                            Text(
                                text = char.toString(),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        // Scroll or filter by letter if wanted
                                        if (char == '#') {
                                            onSearchQueryChanged("")
                                        } else {
                                            onSearchQueryChanged(char.toString())
                                        }
                                    }
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.08f)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SubCategoryChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.06f)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}
