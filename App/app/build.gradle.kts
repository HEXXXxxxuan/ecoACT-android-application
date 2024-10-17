plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

java{
    toolchain{
        languageVersion = JavaLanguageVersion.of(17);
    }
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
    }
    testOptions {
        unitTests.isReturnDefaultValues = true;
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.swiperefreshlayout)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.mpandroidchart)
    implementation(libs.semicirclearcprogressbar)
    implementation(libs.pager.bottom.tab.strip)
    implementation(libs.gson)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.rules)
}