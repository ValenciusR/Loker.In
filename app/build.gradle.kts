plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lokerin.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lokerin.app"
        minSdk = 23
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")

    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-storage")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

}
