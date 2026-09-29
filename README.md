# AnInspector
[![RB Status](https://shields.rbtlog.dev/simple/razertexz.aninspector?style=for-the-badge)](https://shields.rbtlog.dev/razertexz.aninspector)

A minimalist Android package inspector built with Kotlin and Jetpack Compose.

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroidButtonGreyBorder_nofont.png" height="80" alt="Get it on IzzyOnDroid">](https://apt.izzysoft.de/packages/razertexz.aninspector)

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
Requires **JDK 21+**. Output is saved to `app/build/outputs/apk/debug/app-debug.apk`.

### Windows
```powershell
.\gradlew.bat :app:assembleDebug
```

### Linux / macOS
```bash
./gradlew :app:assembleDebug
```
