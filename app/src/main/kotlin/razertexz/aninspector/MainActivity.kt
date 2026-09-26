package razertexz.aninspector

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = ViewModelProvider(this)[MainViewModel::class]

        setContent {
            when (val selected = vm.selectedApp) {
                null -> AppListScreen(vm.apps) { vm.openDetails(it) }
                else -> AppDetailsScreen(selected) { vm.closeDetails() }
            }
        }
    }
}

@Composable
private fun AppListScreen(apps: List<App>, onSelect: (App) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(bgColor).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(apps, key = { it.packageName }) { app ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(surfaceColor).clickable { onSelect(app) }.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(40.dp))
                Column {
                    BasicText(app.label, style = titleStyle)
                    BasicText(app.packageName, style = monoStyle)
                }
            }
        }
    }
}

@Composable
private fun AppDetailsScreen(app: SelectedApp, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(bgColor).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(surfaceColor).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(48.dp))
                Column {
                    BasicText(app.label, style = titleStyle)
                    BasicText(app.packageName, style = monoStyle)
                }
            }
        }

        item {
            Column {
                BasicText("Version:     ${app.versionName} (${app.versionCode})", style = monoStyle)
                BasicText("Target SDK:  ${app.targetSdk.toAndroidVersion()} (${app.targetSdk})", style = monoStyle)
                BasicText("Minimum SDK: ${app.minSdk.toAndroidVersion()} (${app.minSdk})", style = monoStyle)
                BasicText("Size:        ${app.size}", style = monoStyle)
                BasicText("User ID:     ${app.userId}", style = monoStyle)
            }
        }

        item {
            BasicText("PERMISSIONS (${app.permissions.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
        }

        items(app.permissions, key = { it }) {
            BasicText(it.removePrefix("android.permission."), modifier = Modifier.padding(start = 8.dp), style = monoStyle)
        }

        item {
            BasicText("ACTIVITIES (${app.activities.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
        }

        items(app.activities, key = { it.name }) {
            BasicText("${it.name.removePrefix(app.packageName)}${if (it.exported) " [Exported]" else ""}", modifier = Modifier.padding(start = 8.dp), style = monoStyle)
        }
    }
}

private val bgColor = Color(0xFF121212)
private val surfaceColor = Color(0xFF1E1E1E)

private val titleStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
private val monoStyle = TextStyle(color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

private fun Int.toAndroidVersion(): String = when {
    this >= 33 -> "Android ${this - 20}"
    this == 32 -> "Android 12L"
    this >= 28 -> "Android ${this - 19}"
    this == 27 -> "Android 8.1"
    this == 26 -> "Android 8"
    this == 25 -> "Android 7.1"
    this >= 23 -> "Android ${this - 17}"
    this == 22 -> "Android 5.1"
    this == 21 -> "Android 5"
    else -> "N/A"
}