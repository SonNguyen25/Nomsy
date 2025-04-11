@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kapt {
    correctErrorTypes = true
    javacOptions {
        // Pass the export flag so that jdk.compiler exports com.sun.tools.javac.main to the unnamed module.
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")

    }

}

android {
    namespace = "com.example.nomsy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nomsy"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.2" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildToolsVersion = "34.0.0"
    buildFeatures { viewBinding = true }
}
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.runtime.android)
    val room_version = "2.6.1"

        implementation("androidx.room:room-runtime:$room_version")
        kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
        implementation("androidx.room:room-ktx:$room_version")

        // optional - Test helpers
        testImplementation("androidx.room:room-testing:$room_version")

        // optional - Paging 3 Integration
        implementation("androidx.room:room-paging:$room_version")
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Runtime
    implementation(libs.androidx.runtime)
    // Card
    implementation(libs.androidx.material)
    // Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    //Glide Image
    implementation(libs.compose)
    implementation("androidx.compose.ui:ui:<compose_version>")
    implementation("androidx.compose.material:material:<compose_version>")
    implementation("androidx.compose.ui:ui-tooling-preview:<compose_version>")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:<version>")
    implementation("androidx.activity:activity-compose:<version>")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.paging:paging-runtime:3.1.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")



}
