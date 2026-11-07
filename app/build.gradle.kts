plugins { id("com.android.application") }
android {
    namespace = "org.temproot.checker"
    compileSdk = 35
    defaultConfig { applicationId = "org.temproot.checker"; minSdk = 26; targetSdk = 28; versionCode = 1; versionName = "1.0-check-only" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
