plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "coa.appa.azino777"
    compileSdk = 34

    defaultConfig {
        applicationId = "coa.appa.azino777"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.gson)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseMessaging)
    implementation(libs.onesignal)
    implementation(libs.googleServices)
    implementation(libs.afAndroidSdk)
    implementation(libs.androidDeviceNames)
    testImplementation(libs.junit)
    implementation(libs.play.services.ads.identifier)
    androidTestImplementation(libs.androidTestJunit)
    androidTestImplementation(libs.espressoCore)
}