import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    `maven-publish`
}

val githubProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "github.properties")))
}

android {

    compileSdk = ProjectSetting.PROJECT_COMPILE_SDK

    defaultConfig {
        minSdk = ProjectSetting.PROJECT_MIN_SDK
        targetSdk = ProjectSetting.PROJECT_TARGET_SDK

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")

}

afterEvaluate {
    publishing {
        publications {

            // Creates a Maven publication called "release".
            register("release", MavenPublication::class) {

                // Applies the component for the release build variant.
                // NOTE : Delete this line code if you publish Native Java / Kotlin Library
                from(components["release"])

                // Library Package Name (Example : "com.frogobox.androidfirstlib")
                // NOTE : Different GroupId For Each Library / Module, So That Each Library Is Not Overwritten
                groupId = ProjectSetting.PROJECT_LIB_ID_SDK

                // Library Name / Module Name (Example : "androidfirstlib")
                // NOTE : Different ArtifactId For Each Library / Module, So That Each Library Is Not Overwritten
                artifactId = ProjectSetting.MODULE_NAME_SDK

                // Version Library Name (Example : "1.0.0")
                version = ProjectSetting.PROJECT_VERSION_NAME

            }

            repositories {
                maven {
                    name = "GitHubPackages"
                    /** Configure path of your package repository on Github
                     *  Replace GITHUB_USERID with your/organisation Github userID and REPOSITORY with the repository name on GitHub
                     */
                    url = uri("https://maven.pkg.github.com/${ProjectSetting.GITHUB_KEY_ID}/${ProjectSetting.REPOSITORY_NAME}")

                    credentials {
                        /**Create github.properties in root project folder file with gpr.usr=GITHUB_USER_ID  & gpr.key=PERSONAL_ACCESS_TOKEN**/
                        username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER_ID")).toString() // ProjectSetting.GITHUB_KEY_ID
                        password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
                    }
                }
            }

        }
    }
}
