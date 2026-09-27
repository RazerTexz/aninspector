# AnInspector
A minimalist Android package inspector built with Kotlin and Jetpack Compose.

**View installed package details:**

- Target and minimum SDK
- Version name and code
- Size and user ID
- Permissions and activities

<table align="center">
    <tr>
        <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="300" alt="Package list" /></td>
        <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="300" alt="Package details" /></td>
    </tr>
</table>

## Building
Requires JDK 21.

### Windows
```cmd
.\gradlew assembleDebug & rem Output: app/build/outputs/apk/debug/app-debug.apk
```

### Linux / macOS
```bash
./gradlew assembleDebug # Output: app/build/outputs/apk/debug/app-debug.apk
```