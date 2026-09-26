package razertexz.aninspector

import android.app.Application
import android.content.pm.ActivityInfo
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

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    var apps by mutableStateOf<List<App>>(emptyList())
    var selectedApp by mutableStateOf<SelectedApp?>(null)

    init {
        load()
    }

    fun load() {
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

    fun select(app: App) {
        viewModelScope.launch(Dispatchers.IO) {
            val info = application.packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES)
            selectedApp = SelectedApp(
                packageName = app.packageName,
                label = app.label,

                versionName = info.versionName ?: "N/A",
                versionCode = info.longVersionCode,

                targetSdk = info.applicationInfo?.targetSdkVersion ?: 0,
                minSdk = info.applicationInfo?.minSdkVersion ?: 0,

                size = info.applicationInfo?.sourceDir?.let { Formatter.formatFileSize(application, File(it).length()) } ?: "N/A",
                userId = info.applicationInfo?.uid ?: 0,

                permissions = info.requestedPermissions?.sorted() ?: emptyList(),
                activities = info.activities?.sortedBy { it.name } ?: emptyList(),

                icon = app.icon
            )
        }
    }
}