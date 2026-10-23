plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "jp.co.keyplanning.okinawaitfes"
    compileSdk = 35
    defaultConfig {
        applicationId = "jp.co.keyplanning.okinawaitfes"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    lint { warningsAsErrors = true }
}

dependencies {
    //noinspection GradleDependency
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    //noinspection GradleDependency
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    testImplementation("junit:junit:4.13.2")
    //noinspection GradleDependency
    androidTestImplementation("androidx.test:runner:1.6.2")
    //noinspection GradleDependency
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
