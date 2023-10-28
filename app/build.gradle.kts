plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.barterbuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.barterbuddy"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Mockito test implementation to mock Firestore and Activities during unit testing
    testImplementation("org.mockito:mockito-core:2.19.0")
    // Firebase BoM for version consistency
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    // Firestore library
    implementation("com.google.firebase:firebase-firestore")
    // Cloud storage library
    implementation("com.google.firebase:firebase-storage")
    // FirebaseUI (for chat message recycler)
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
}