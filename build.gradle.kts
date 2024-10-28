buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.androidx.navigation.safeArgs.gradlePlugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
