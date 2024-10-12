plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.go4.application"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.go4.application"
        minSdk = 33
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
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation(libs.swiperefreshlayout)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.mpandroidchart)
    testImplementation(libs.mockito.core)
    testImplementation (libs.mockito.inline)
    androidTestImplementation(libs.espresso.core.v340)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.junit.v113)
    implementation(libs.semicirclearcprogressbar.v111)
    implementation ("me.majiajie:pager-bottom-tab-strip:2.4.0")
    implementation("com.google.code.gson:gson:2.8.8")


}