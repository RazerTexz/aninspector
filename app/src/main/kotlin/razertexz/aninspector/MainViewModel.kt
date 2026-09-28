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

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    private var installedPackages = listOf<Package>()

    var packages by mutableStateOf<List<Package>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
        private set

    init {
        loadPackages()
    }

    fun loadPackages() {
        viewModelScope.launch(Dispatchers.IO) {
            installedPackages = app.packageManager.getInstalledApplications(0).map {
                Package(
                    packageName = it.packageName,
                    label = it.loadLabel(app.packageManager).toString(),
                    icon = it.loadIcon(app.packageManager).toBitmap().asImageBitmap()
                )
            }.sortedBy { it.label.lowercase() }
            packages = installedPackages
        }
    }

    fun search(query: String) {
        searchQuery = query
        packages = if (query.isBlank()) {
            installedPackages
        } else {
            installedPackages.filter {
                it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
            }
        }
    }


    var selectedPackage by mutableStateOf<SelectedPackage?>(null)
        private set

    fun select(pkg: Package) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pkgInfo = app.packageManager.getPackageInfo(pkg.packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES)
                val appInfo = pkgInfo.applicationInfo

                selectedPackage = SelectedPackage(
                    packageName = pkg.packageName,
                    label = pkg.label,
                    versionName = pkgInfo.versionName ?: "N/A",
                    versionCode = pkgInfo.longVersionCode,
                    targetSdk = appInfo?.targetSdkVersion ?: 0,
                    minSdk = appInfo?.minSdkVersion ?: 0,
                    size = appInfo?.formattedSize() ?: "N/A",
                    userId = appInfo?.uid ?: 0,
                    permissions = pkgInfo.requestedPermissions?.sorted() ?: emptyList(),
                    activities = pkgInfo.activities?.sortedBy { it.name } ?: emptyList(),
                    icon = pkg.icon
                )
            } catch (e: PackageManager.NameNotFoundException) {
                loadPackages()
            }
        }
    }

    fun deselect() {
        selectedPackage = null
    }

    private fun ApplicationInfo.formattedSize(): String {
        val baseBytes = sourceDir?.let { File(it).length() } ?: 0L
        val splitBytes = splitSourceDirs?.sumOf { File(it).length() } ?: 0L

        return Formatter.formatFileSize(app, baseBytes + splitBytes)
    }
}

data class Package(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap
)

data class SelectedPackage(
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