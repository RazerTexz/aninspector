package razertexz.aninspector

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.text.format.Formatter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import java.io.File

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    var apps by mutableStateOf<List<App>>(emptyList())

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            apps = application.packageManager.getInstalledApplications(0).map {
                App(
                    packageName = it.packageName,
                    label = it.loadLabel(application.packageManager).toString(),
                    icon = it.loadIcon(application.packageManager).toBitmap().asImageBitmap()
                )
            }.sortedBy { it.label.lowercase() }
        }
    }


    var selectedApp by mutableStateOf<SelectedApp?>(null)
    fun openDetails(app: App) {
        viewModelScope.launch(Dispatchers.IO) {
            val pkgInfo = application.packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES)
            val appInfo = pkgInfo.applicationInfo

            selectedApp = SelectedApp(
                packageName = app.packageName,
                label = app.label,

                versionName = pkgInfo.versionName ?: "N/A",
                versionCode = pkgInfo.longVersionCode,

                targetSdk = appInfo?.targetSdkVersion ?: 0,
                minSdk = appInfo?.minSdkVersion ?: 0,

                size = appInfo?.formattedSize() ?: "N/A",
                userId = appInfo?.uid ?: 0,

                permissions = pkgInfo.requestedPermissions?.sorted() ?: emptyList(),
                activities = pkgInfo.activities?.sortedBy { it.name } ?: emptyList(),

                icon = app.icon
            )
        }
    }

    fun closeDetails() {
        selectedApp = null
    }

    private fun ApplicationInfo.formattedSize(): String {
        val baseBytes = sourceDir?.let { File(it).length() } ?: 0L
        val splitBytes = splitSourceDirs?.sumOf { File(it).length() } ?: 0L

        return Formatter.formatFileSize(application, baseBytes + splitBytes)
    }
}

data class App(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap
)

data class SelectedApp(
    val packageName: String,
    val label: String,

    val versionName: String,
    val versionCode: Long,

    val targetSdk: Int,
    val minSdk: Int,

    val size: String,
    val userId: Int,

    val permissions: List<String>,
    val activities: List<ActivityInfo>,

    val icon: ImageBitmap
)