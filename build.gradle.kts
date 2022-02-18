buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
    }
}

allprojects {
    repositories {
        maven {
            setUrl("https://jitpack.io")
        }
        mavenCentral()
        google()
    }
}

tasks.wrapper {
    enabled = false
}