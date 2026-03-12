plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
}

// Define version components
val versionMajor = 1
val versionMinor = 3
val versionPatch = 0
val isBeta = true

android {
    namespace = "com.roaa.playbox"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.roaa.playbox"
        minSdk = 26
        targetSdk = 36
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName =
            "${versionMajor}.${versionMinor}.${versionPatch}" + if (isBeta) "-beta" else ""
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "(Debug)")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
}

tasks.withType<com.android.build.gradle.tasks.PackageAndroidArtifact> {
    // no-op
}

// Add this at top level
val versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
val versionName = "${versionMajor}.${versionMinor}.${versionPatch}" + if (isBeta) "-beta" else ""

base.archivesName.set("pb-v$versionCode($versionName)")

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.navigation3.ui)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.bundles.media3)
    implementation(libs.accompanist.permissions)

    // For loading thumbnails (optional)
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.kotlinx.serialization.json)
}