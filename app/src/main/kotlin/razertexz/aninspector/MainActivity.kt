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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
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
            when (val sel = vm.selectedPackage) {
                null -> ListScreen(vm.packages, searchQuery = vm.searchQuery, onSearch = { vm.search(it) }) {
                    vm.select(it)
                }
                else -> DetailsScreen(sel) {
                    vm.deselect()
                }
            }
        }
    }
}

@Composable
private fun ListScreen(packages: List<Package>, searchQuery: String, onSearch: (String) -> Unit, onSelect: (Package) -> Unit) {
    BackHandler(enabled = searchQuery.isNotEmpty()) {
        onSearch("")
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            searchQuery, onSearch,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(containerColor).padding(12.dp),
            textStyle = titleStyle,
            singleLine = true,
            cursorBrush = SolidColor(Color.White),
        ) {
            if (searchQuery.isEmpty()) {
                BasicText("Search packages...", style = secondaryStyle)
            }

            it()
        }

        BasicText("PACKAGES (${packages.size})", style = titleStyle)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(packages, key = { it.packageName }) { pkg ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(surfaceColor).clickable { onSelect(pkg) }.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(bitmap = pkg.icon, contentDescription = null, modifier = Modifier.size(40.dp))
                    Column {
                        BasicText(pkg.label, style = titleStyle)
                        BasicText(pkg.packageName, style = monoStyle)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsScreen(selectedPackage: SelectedPackage, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(backgroundColor).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(surfaceColor).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(bitmap = selectedPackage.icon, contentDescription = null, modifier = Modifier.size(48.dp))
                Column {
                    BasicText(selectedPackage.label, style = titleStyle)
                    BasicText(selectedPackage.packageName, style = monoStyle)
                }
            }
        }

        item {
            Column {
                BasicText("Version:     ${selectedPackage.versionName} (${selectedPackage.versionCode})", style = monoStyle)
                BasicText("Target SDK:  ${selectedPackage.targetSdk.toAndroidVersion()} (${selectedPackage.targetSdk})", style = monoStyle)
                BasicText("Minimum SDK: ${selectedPackage.minSdk.toAndroidVersion()} (${selectedPackage.minSdk})", style = monoStyle)
                BasicText("Size:        ${selectedPackage.size}", style = monoStyle)
                BasicText("User ID:     ${selectedPackage.userId}", style = monoStyle)
            }
        }

        item {
            BasicText("PERMISSIONS (${selectedPackage.permissions.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
        }

        items(selectedPackage.permissions, key = { it }) {
            BasicText(it.removePrefix("android.permission."), modifier = Modifier.padding(start = 8.dp), style = monoStyle)
        }

        item {
            BasicText("ACTIVITIES (${selectedPackage.activities.size})", modifier = Modifier.padding(top = 8.dp), style = titleStyle)
        }

        items(selectedPackage.activities, key = { it.name }) {
            BasicText("${it.name.removePrefix(selectedPackage.packageName)}${if (it.exported) " [Exported]" else ""}", modifier = Modifier.padding(start = 8.dp), style = monoStyle)
        }
    }
}

private val backgroundColor = Color(0xFF121212)
private val surfaceColor = Color(0xFF1E1E1E)
private val containerColor = Color(0xFF2C2C2C)

private val titleStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
private val secondaryStyle = TextStyle(color = Color(0xFFA0A0A0), fontSize = 16.sp)
private val monoStyle = TextStyle(color = Color(0xFFA0A0A0), fontSize = 12.sp, fontFamily = FontFamily.Monospace)

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