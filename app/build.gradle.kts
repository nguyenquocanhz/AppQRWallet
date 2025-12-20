plugins {
    alias(libs.plugins.android.application)
    // Add Google Services plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.nqatech.vqr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nqatech.vqr"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    
    // CameraX dependencies
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    
    // ZXing for QR decoding
    implementation(libs.zxing.core)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    
    // Biometric
    implementation(libs.biometric)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Firebase (Import the BoM for the Firebase platform)
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    // Declare the dependencies for the Firebase Cloud Messaging library
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database") // Added for Realtime Database
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}