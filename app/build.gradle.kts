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
    compileSdk = 30
    defaultConfig {
        applicationId = "crazyboyfeng.accSettings"
        minSdk = 14
        targetSdk = 30
        versionCode = 20210819
        versionName = "2021.8.19"
//        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        resValue("string", "version_name", versionName!!)
    }
//    buildFeatures { viewBinding = true }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlinOptions { jvmTarget = "1.8" }
}

dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation("androidx.core:core-ktx:1.6.0")
//    implementation("androidx.appcompat:appcompat:1.3.1")
//    implementation("com.google.android.material:material:1.4.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.work:work-runtime:2.5.0")
    implementation("com.github.topjohnwu.libsu:core:3.1.2")
    val axpeVersion = "0.4.9"
    implementation("com.github.CrazyBoyFeng.AndroidXPreferenceExtensions:edittext:$axpeVersion")
    implementation("com.github.CrazyBoyFeng.AndroidXPreferenceExtensions:numberpicker:$axpeVersion")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.3")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}