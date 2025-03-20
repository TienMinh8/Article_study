plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.article"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.article"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // Retrofit and Gson for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Facebook Shimmer for loading effects
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    
    // SwipeRefreshLayout for pull-to-refresh feature
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.facebook.android:facebook-android-sdk:latest.release")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}