plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "dam.pmpd.rickymortyseguimiento"
    compileSdk = 34

    defaultConfig {
        applicationId = "dam.pmpd.rickymortyseguimiento"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

// --- LIBRERÍAS BÁSICAS DE ANDROID (Versiones Estables para API 34) ---
    implementation("androidx.core:core-ktx:1.13.1") // Esta era la culpable (antes tenías 1.17.0)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Actividad y Fragmentos
    implementation("androidx.activity:activity-ktx:1.9.0") // Versión estable
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    // --- FIREBASE (Lo que ya tenías) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // BoM actualizado
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // --- RETROFIT & GSON (Para la API de Rick y Morty) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- GLIDE (Para imágenes) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- NAVEGACIÓN ---
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // --- TESTING (Déjalo por defecto) ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


}