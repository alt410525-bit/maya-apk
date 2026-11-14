plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val roomVersion = "2.6.1"
val hiltVersion = "2.51.1"

android {
    namespace = "com.maya.clone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.maya.clone"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Compose Compiler 1.5.5 is the version that matches Kotlin 1.9.20
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room (KSP)
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Hilt (KSP, no kapt)
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Gson (TypeConverters)
    implementation("com.google.code.gson:gson:2.10.1")

    // ML Kit (face detection). Speech and TTS use Android system APIs.
    implementation("com.google.mlkit:face-detection:16.0.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Encryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coil (image loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Play Billing
    implementation("com.android.billingclient:billing:7.0.0")

    // FCM
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
}
