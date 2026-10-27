package com.fastlauncher.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fastlauncher.app.ui.viewmodel.LauncherViewModel

@Composable
fun MainLauncherScreen(
    viewModel: LauncherViewModel,
    onOpenApp: (Intent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            apps = viewModel.apps,
            onOpenApp = onOpenApp,
            onLaunchAppIntent = { pkg, act -> viewModel.launchApp(pkg, act) },
            onOpenDrawer = { viewModel.isDrawerOpen = true }
        )

        DrawerScreen(
            isOpen = viewModel.isDrawerOpen,
            apps = viewModel.apps,
            searchQuery = viewModel.searchQuery,
            onSearchQueryChanged = { viewModel.searchQuery = it },
            onCloseDrawer = { viewModel.isDrawerOpen = false },
            onOpenApp = onOpenApp,
            onLaunchAppIntent = { pkg, act -> viewModel.launchApp(pkg, act) }
        )
    }
}
