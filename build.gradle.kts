// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
    id("androidx.room") version "2.6.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.5" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}

buildscript {
    dependencies {
        classpath (libs.androidx.navigation.safe.args.gradle.plugin)
        classpath (libs.com.google.devtools.ksp.gradle.plugin)
    }
}