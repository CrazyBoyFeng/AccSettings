plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    /**
     * Personal opinion:
     * compileSdk doesn't make sense,
     * just generally be consistent with targetSdk.
     */
    compileSdk = 32
    defaultConfig {
        applicationId = "crazyboyfeng.accSettings"
        minSdk = 14
        targetSdk = 32
        versionCode = 202109202
        versionName = "2021.9.20.2"
        resValue("string", "version_name", versionName!!)
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlinOptions { jvmTarget = "1.8" }
}

dependencies {
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation("com.github.topjohnwu.libsu:core:3.2.1")
    val axpeVersion = "0.8.2"
    implementation("com.github.CrazyBoyFeng.AndroidXPreferenceExtensions:edittext:$axpeVersion")
    implementation("com.github.CrazyBoyFeng.AndroidXPreferenceExtensions:numberpicker:$axpeVersion")
}