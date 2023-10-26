// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}

plugins {
    id("com.android.application") version "8.1.2" apply false

    // Add the dependency for the Google services Gradle plugin --> Firebase Messaging
    id("com.google.gms.google-services") version "4.4.0" apply false
}