package com.fastlauncher.app.data

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable?
)

class AppRepository(private val context: Context) {
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(intent, 0)
        resolveInfos.map { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo
            val label = resolveInfo.loadLabel(pm).toString()
            val packageName = activityInfo.packageName
            val activityName = activityInfo.name
            val icon = resolveInfo.loadIcon(pm)

            AppInfo(
                label = label,
                packageName = packageName,
                activityName = activityName,
                icon = icon
            )
        }.sortedBy { it.label.lowercase() }
    }
}
