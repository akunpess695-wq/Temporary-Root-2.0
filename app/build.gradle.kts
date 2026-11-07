plugins { id("com.android.application") }
android {
    namespace = "org.temproot.checker"
    compileSdk = 35
    defaultConfig { applicationId = "org.temproot.checker"; minSdk = 26; targetSdk = 35; versionCode = 3; versionName = "2.1" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
