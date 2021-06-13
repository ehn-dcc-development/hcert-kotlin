plugins {
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("idea")
    id("com.github.jk1.dependency-license-report") version Versions.licenseReport
    id("maven-publish")
}

group = "ehn.techiop.hcert"
version = "1.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
licenseReport {
    allowedLicensesFile = File("$projectDir/allowed-licenses.json")
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
                    "-Xopt-in=kotlin.time.ExperimentalTime",
                    "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
                    "-Xopt-in=kotlin.js.ExperimentalJsExport"
                )
            }
        }
    }
    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
            useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
            useExperimentalAnnotation("io.kotest.common.ExperimentalKotest")
            useExperimentalAnnotation("kotlin.js.ExperimentalJsExport")
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
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(LEGACY) {
        moduleName = "hcert"
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = false
                }
            }
            webpackTask {
                output.library = "hcert"
                output.libraryTarget = "umd"
            }
        }
        binaries.executable()
        useCommonJs()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                //cannot use 0.2.1 due to https://youtrack.jetbrains.com/issue/KT-43237 when also seeking to expose node module
                //however, we only release bundles and can monkey-patch it (see webpack.config.d/patch.js)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:${Versions.serialization}")
                implementation("io.github.aakira:napier:${Versions.logging}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
                implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
                implementation("io.kotest:kotest-framework-datatest:${Versions.kotest}")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
                implementation("com.augustcellars.cose:cose-java:${Versions.jvm.cose}")
                implementation("com.google.zxing:core:${Versions.jvm.zxing}")
                implementation("com.google.zxing:javase:${Versions.jvm.zxing}")
                implementation("org.bouncycastle:bcpkix-jdk15to18:${Versions.jvm.bcpkix}")
                implementation("javax.validation:validation-api:${Versions.jvm.validation}")
                implementation("net.pwall.json:json-kotlin-schema:${Versions.jvm.jsonSchema}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
            }
        }
        val jsMain by getting {
            sourceSets { kotlin.srcDir("src/jsMain/generated") }
            dependencies {
                implementation(npm("pako", Versions.js.pako))
                implementation(npm("@types/pako", Versions.js.pakoTypes, generateExternals = true))
                implementation(npm("pkijs", Versions.js.pkijs))
                implementation(npm("cose-js", File("${projectDir.absolutePath}/cose-js"), generateExternals = false))
                implementation(npm("crypto-browserify", Versions.js.`crypto-browserify`))
                implementation(npm("stream-browserify", Versions.js.`stream-browserify`))
                implementation(npm("util", Versions.js.util))
                implementation(npm("buffer", Versions.js.buffer))
                implementation(npm("process", Versions.js.process))
                implementation(npm("cbor", Versions.js.cbor))
                implementation(npm("node-inspect-extracted", Versions.js.`node-inspect-extract`))
                implementation(npm("fast-sha256", Versions.js.sha256, generateExternals = true))
                implementation(npm("url", Versions.js.url))
                implementation(npm("elliptic", Versions.js.elliptic))
                implementation(npm("node-rsa", Versions.js.rsa))
                implementation(npm("constants-browserify", Versions.js.`constants-browserify`))
                implementation(npm("assert", Versions.js.assert))
                implementation(npm("ajv", Versions.js.ajv))
                implementation(npm("ajv-formats", Versions.js.`ajv-formats`))
                implementation(npm("@nuintun/qrcode", Versions.js.qrcode))
            }
        }
        val jsTest by getting {
            sourceSets { kotlin.srcDir("src/jsTest/generated") }
        }
    }
}


publishing {
    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ehn-dcc-development/hcert-kotlin")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}


/*
* KJS: No way to get test resources in a multiplatform project.
* https://youtrack.jetbrains.com/issue/KT-36824
*
* These tasks work around this glaring issue by wrapping test resources into code and providing accessors
* requires base64 decoding afterwards and can surely be optimized, but at least it provides access to test resources
*
* Bonus Issue: this also affects main resources, bc test cases can obviously call code which already depends on resources
* Therefore, we also use the trick in jsMain
* */

tasks.named("clean") { dependsOn(tasks.named("jsCleanResources")) }
tasks.named("compileKotlinJs") { dependsOn(tasks.named("jsWrapMainResources")) }
tasks.named("compileTestKotlinJs") { dependsOn(tasks.named("jsWrapTestResources")) }

tasks.register("jsWrapTestResources") { doFirst { wrapJsResources(test = true) } }
tasks.register("jsWrapMainResources") { doFirst { wrapJsResources() } }
tasks.register("jsCleanResources") {
    File("${projectDir.absolutePath}/src/jsTest/generated").deleteRecursively()
    File("${projectDir.absolutePath}/src/jsMain/generated").deleteRecursively()
}


fun wrapJsResources(test: Boolean = false) {
    val prefix = if (test) "Test" else "Main"
    println("Wrapping $prefix resources into ${prefix}ResourceHolder.kt")

    val dir = File("${projectDir.absolutePath}/src/js${prefix}/generated")
    if (!dir.exists()) {
        if (!dir.mkdirs()) {
            throw Throwable("Could not create generated sources folder")
        }
    }
    val f = File("${projectDir.absolutePath}/src/js${prefix}/generated/${prefix}ResourceHolder.kt")
    f.delete()
    f.createNewFile()
    if (!f.canWrite()) {
        throw Throwable("Could not write generated source file $f")
    }
    f.writer().use { w ->
        if (!test) {
            w.write("interface R { fun get(key: String): String?;fun allResourceNames():List<String>}")
        }
        w.write(
            "object ${prefix}ResourceHolder:R{private val m = mutableMapOf<String,String>();init{\n"
        )

        val basePath = "${projectDir.absolutePath}/src/common${prefix}/resources"
        val baseFile = File(basePath)
        baseFile.walkBottomUp().filter { !it.isDirectory }.forEach {
            val encodeBase64 =
                de.undercouch.gradle.tasks.download.org.apache.commons.codec.binary.Base64.encodeBase64(it.readBytes())
            val key = it.absolutePath.substring(baseFile.absolutePath.length + 1)
            val safeKey = key.replace("\$", "\\\$").replace("\\", "/")
            w.write(
                "m[\"$safeKey\"]=\"" + String(encodeBase64) + "\"\n"
            )
        }
        w.write("}override fun get(key:String)=m[key];")
        w.write("override fun allResourceNames()=m.keys.sorted()}")
    }
}
