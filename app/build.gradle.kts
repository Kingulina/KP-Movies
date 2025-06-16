// app/build.gradle.kts
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

val omdbKey = gradleLocalProperties(rootDir, providers)
    .getProperty("omdbKey") ?: error("Dodaj omdbKey do local.properties")

android {
    namespace   = "com.example.kpmovies"
    compileSdk  = 35

    defaultConfig {
        applicationId         = "com.example.kpmovies"
        minSdk                = 31
        targetSdk             = 35
        versionCode           = 1
        versionName           = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /**  ▼▼ BuildConfig.OMDB_KEY  */
        buildConfigField("String", "OMDB_KEY", "\"$omdbKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions { jvmTarget = "11" }

    buildFeatures {
        compose       = true
        viewBinding   = true
        buildConfig   = true         //  ← konieczne dla BuildConfig
    }
}

/* ----------  dependencies pominięte (twoje pozostają bez zmian)  ---------- */

kapt {
    correctErrorTypes      = true
    includeCompileClasspath = true
    /*  ▼ zapis schem Room do /app/schemas  */
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}