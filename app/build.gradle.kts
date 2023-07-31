/*
 * Copyright © 2023 J!nl!n™ Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    jacoco
}

android {
    namespace = "com.l3gacy.app.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.l3gacy.app.sample"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.withType<JacocoReport> {
    reports {
        csv.required.set(false)
        html.required.set(true)
        xml.required.set(true)
    }
}

val jacocoTestReport = tasks.create("jacocoTestReport")

androidComponents.onVariants { variant ->
    val testTaskName = "test${variant.name.capitalize()}UnitTest"
    val reportTask = tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class) {
        dependsOn(testTaskName)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        classDirectories.setFrom(
            fileTree("$buildDir/tmp/kotlin-classes/${variant.name}") {
                exclude(listOf(
                    // Android
                    "**/R.class",
                    "**/R\$*.class",
                    "**/BuildConfig.*",
                    "**/Manifest*.*",
                ))
            },
        )

        sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))
        executionData.setFrom(file("$buildDir/jacoco/$testTaskName.exec"))
    }
    jacocoTestReport.dependsOn(reportTask)
}

tasks.withType<Test>().configureEach {
    configure<JacocoTaskExtension> {
        // Required for JaCoCo + Robolectric
        // https://github.com/robolectric/robolectric/issues/2230
        // TODO: Consider removing if not we don't add Robolectric
        isIncludeNoLocationClasses = true

        // Required for JDK 11 with the above
        // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
        excludes = listOf("jdk.internal.*")
    }
}
