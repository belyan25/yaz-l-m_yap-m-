// Project seviyesi build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    // Eğer projende Kotlin de varsa alt satırda o da olmalı:
    // alias(libs.plugins.jetbrains.kotlin.android) apply false
}