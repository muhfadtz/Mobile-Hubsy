plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.hubsytesting"
    compileSdk = 35 // API 35 belum stabil, gunakan API 34

    defaultConfig {
        applicationId = "com.example.hubsytesting"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // ✅ Aktifkan View Binding dengan benar
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ✅ Gunakan hanya satu cara: katalog versi (libs.versions.toml)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ✅ Tambahkan ke katalog versi agar konsisten
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx)

    // ✅ Tambahkan di libs.versions.toml
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
