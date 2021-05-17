plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    id("idea")
}

group = "ehn.techiop.hcert"
version = "0.3.0-SNAPSHOT"

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

kotlin {
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
                    "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
                )
            }
        }
    }
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict"
                )
            }
        }
    }
    js(LEGACY) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = false
                }
            }
        }
        useCommonJs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0")
                implementation("com.augustcellars.cose:cose-java:1.1.0")
                implementation("com.google.zxing:core:3.4.1")
                implementation("com.google.zxing:javase:3.4.1")
                implementation("org.bouncycastle:bcpkix-jdk15to18:1.68")
                implementation("javax.validation:validation-api:2.0.1.Final")
                implementation("com.google.code.findbugs:jsr305:3.0.2")
                implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.12.3")
                implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.3")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
                implementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
                implementation("org.hamcrest:hamcrest:2.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "2.0.3"))
                implementation(npm("@types/pako", "1.0.1", generateExternals = true))

            }
        }
        val jsTest by getting
    }
}
