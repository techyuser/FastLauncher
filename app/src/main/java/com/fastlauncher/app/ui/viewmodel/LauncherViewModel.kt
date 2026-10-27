package com.fastlauncher.app.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fastlauncher.app.data.AppInfo
import com.fastlauncher.app.data.AppRepository
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    var apps by mutableStateOf<List<AppInfo>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
    var isDrawerOpen by mutableStateOf(false)

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            apps = repository.getInstalledApps()
        }
    }

    fun launchApp(packageName: String, activityName: String): Intent? {
        val pm = getApplication<Application>().packageManager
        return pm.getLaunchIntentForPackage(packageName) ?: Intent().apply {
            setClassName(packageName, activityName)
        }
    }
}
