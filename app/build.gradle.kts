plugins {
    id("com.android.application") version "9.4.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
}

android {
    namespace = "razertexz.aninspector"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
        targetSdk = 37
        versionCode = 111
        versionName = "1.1.1"
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("RELEASE_KEYSTORE_PASSWORD")?.let { file("release.jks") }
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")

            keyAlias = "release"
            keyPassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    dependenciesInfo {
        includeInApk = false
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.foundation:foundation:1.12.1")
}