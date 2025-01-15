import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.hiltPlugin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.jetbrains.dokka)
    id("kotlin-parcelize")
}

android {
    namespace = "com.android.securesms.service"
    compileSdk = 35

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 35
        versionCode = 1
         versionName = "1.0.0.a1" //(Major.Minor.Patch.Design)
//        versionName = "0.dev.testing"
        setProperty("archivesBaseName", "securesms-$versionName")
        vectorDrawables.useSupportLibrary = true
        renderscriptTargetApi = 24
        renderscriptSupportModeEnabled = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    packaging {
        jniLibs.useLegacyPackaging = false
        resources {
            excludes += listOf("META-INF/LICENSE", "META-INF/NOTICE", "META-INF/java.properties")
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        ignoreWarnings = true
        checkDependencies = true
        warningsAsErrors = false
    }

    allprojects {
        gradle.projectsEvaluated {
            tasks.withType<JavaCompile> {
                options.compilerArgs.add("-Xlint:unchecked")
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.toolargetool)

    implementation(libs.androidx.multidex)
    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.firebase.messaging)

    // Room components
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.androidx.room.testing)

    // Lifecycle components
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.common.java8)

    dokkaPlugin(libs.android.documentation.plugin)

    implementation(libs.timber)

    implementation(libs.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)

    debugImplementation(libs.leakcanary.android)
}

dokka {
    moduleName.set("Sms")

    dokkaSourceSets.main {
        suppress.set(false)
        displayName.set("Sms")

        documentedVisibilities(
            VisibilityModifier.Public,
            VisibilityModifier.Private,
            VisibilityModifier.Protected,
            VisibilityModifier.Internal,
            VisibilityModifier.Package
        )

        reportUndocumented.set(false)
        skipEmptyPackages.set(true)
        skipDeprecated.set(false)
        suppressGeneratedFiles.set(true)
    }

    dokkaPublications.html {
        outputDirectory.set(file("../docs"))
    }
    pluginsConfiguration.html {
        footerMessage.set("© 2025 Rossyn :: Sms (${project.android.defaultConfig.versionName})")
//        customAssets.from(file("src/main/res/drawable/camera_icon.webp"))
    }
}