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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
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

        val bgColor = Color(0xFF121212)
        val surfaceColor = Color(0xFF1E1E1E)

        val titleStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        val monoStyle = TextStyle(color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

        setContent {
            val selectedApp = vm.selectedApp
            if (selectedApp == null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(bgColor).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.apps, key = { it.packageName }) { app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(surfaceColor).clickable { vm.select(app) }.padding(8.dp),
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
            } else {
                BackHandler { vm.selectedApp = null }
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
                            Image(bitmap = selectedApp.icon, contentDescription = null, modifier = Modifier.size(48.dp))
                            Column {
                                BasicText(selectedApp.label, style = titleStyle)
                                BasicText(selectedApp.packageName, style = monoStyle)
                            }
                        }
                    }

                    item {
                        Column {
                            BasicText("Version:     ${selectedApp.versionName} (${selectedApp.versionCode})", style = monoStyle)
                            BasicText("Target SDK:  Android ${androidVersion(selectedApp.targetSdk)} (${selectedApp.targetSdk})", style = monoStyle)
                            BasicText("Minimum SDK: Android ${androidVersion(selectedApp.minSdk)} (${selectedApp.minSdk})", style = monoStyle)
                            BasicText("Size:        ${selectedApp.size}", style = monoStyle)
                            BasicText("User ID:     ${selectedApp.userId}", style = monoStyle)
                        }
                    }

                    item {
                        BasicText("PERMISSIONS (${selectedApp.permissions.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
                    }

                    items(selectedApp.permissions, key = { it }) {
                        BasicText(it.removePrefix("android.permission."), modifier = Modifier.padding(start = 8.dp), style = monoStyle)
                    }

                    item {
                        BasicText("ACTIVITIES (${selectedApp.activities.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
                    }

                    items(selectedApp.activities, key = { it.name }) {
                        BasicText("${it.name.removePrefix(selectedApp.packageName)}${if (it.exported) " [Exported]" else ""}", modifier = Modifier.padding(start = 8.dp), style = monoStyle)
                    }
                }
            }
        }
    }
    
    private fun androidVersion(sdk: Int): String = when {
        sdk >= 33 -> "${sdk - 20}"
        sdk == 32 -> "12L"
        sdk >= 28 -> "${sdk - 19}"
        sdk == 27 -> "8.1"
        sdk == 26 -> "8"
        sdk == 25 -> "7.1"
        sdk >= 23 -> "${sdk - 17}"
        sdk == 22 -> "5.1"
        sdk == 21 -> "5"
        else -> "N/A"
    }
}