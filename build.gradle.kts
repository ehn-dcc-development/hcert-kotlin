plugins {
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("idea")
    id("com.github.jk1.dependency-license-report") version Versions.licenseReport
    id("maven-publish")
}

group = "ehn.techiop.hcert"
version = "1.3.0"

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

object customSrcDirs {
    val commonShared = "src/commonShared/kotlin"
    val jsMainGenerated = "src/jsMain/generated"
    val jsTestGenerated = "src/jsTest/generated"
    val jvmFaulty = "src/jvmMain/datagen"
}

val faultAttribute = Attribute.of("ehn.techiop.hcert.faults", String::class.java)

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
        attributes.attribute(faultAttribute, "false")
    }

    jvm("jvmDataGen") {
        compilations {
            val main by compilations.getting {
                kotlinOptions {
                    jvmTarget = "1.8"
                    freeCompilerArgs = listOf(
                        "-Xjsr305=strict"
                    )
                }
                defaultSourceSet {
                    dependsOn(sourceSets.getByName("jvmMain"))
                    kotlin.srcDir(customSrcDirs.jvmFaulty)
                }
            }
        }
        attributes.attribute(faultAttribute, "true")
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
        val commonShared by creating {
            sourceSets { kotlin.srcDir(customSrcDirs.commonShared) }
        }
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:${Versions.serialization}")
                api("io.github.aakira:napier:${Versions.logging}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
                implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
                implementation("io.kotest:kotest-framework-datatest:${Versions.kotest}")
            }
            dependsOn(commonShared)
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
                implementation("com.augustcellars.cose:cose-java:${Versions.jvm.cose}")
                implementation("com.google.zxing:core:${Versions.jvm.zxing}")
                implementation("com.google.zxing:javase:${Versions.jvm.zxing}")
                implementation("org.bouncycastle:bcpkix-jdk15to18:${Versions.jvm.bcpkix}")
                implementation("net.pwall.json:json-kotlin-schema:${Versions.jvm.jsonSchema}")
                // explicit declaration to overrule subdependency version
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
            }
        }
        val jvmDataGenMain by getting {
            dependsOn(commonShared)
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
            }
            dependsOn(jvmDataGenMain)
        }

        val jsMain by getting {
            sourceSets { kotlin.srcDir(customSrcDirs.jsMainGenerated) }
            dependencies {
                implementation(npm("pako", Versions.js.pako))
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
            sourceSets { kotlin.srcDir(customSrcDirs.jsTestGenerated) }
        }
    }
}

tasks {
    /*
     * KJS: No way to get test resources in a multiplatform project.
     * https://youtrack.jetbrains.com/issue/KT-36824
     *
     * These tasks work around this glaring issue by wrapping test resources into code and providing accessors
     * requires base64 decoding afterwards and can surely be optimized, but at least it provides access to test resources
     *
     * Bonus Issue: this also affects main resources, bc test cases can obviously call code which already depends on resources
     * Therefore, we also use the trick in jsMain
     */
    fun wrapJsResources(test: Boolean = false) {
        val (prefix, srcDir) = if (test)
            ("Test" to customSrcDirs.jsTestGenerated)
        else
            ("Main" to customSrcDirs.jsMainGenerated)
        logger.info("Wrapping $prefix resources into $srcDir/${prefix}ResourceHolder.kt")

        val dir = File("${projectDir.absolutePath}/$srcDir").also {
            if (!it.exists() && !it.mkdirs())
                throw Throwable("Could not create generated sources folder $it")
        }
        val f = File(dir, "${prefix}ResourceHolder.kt").also {
            it.delete()
            it.createNewFile()
            if (!it.canWrite())
                throw Throwable("Could not write generated source file $it")
        }
        f.writer().use { w ->
            if (!test)
                w.write("interface R {\nfun get(key: String): String?\nfun allResourceNames(): List<String>\n}\n")
            w.write("object ${prefix}ResourceHolder:R {\nprivate val m = mutableMapOf<String,String>()\ninit{\n")

            val basePath = "${projectDir.absolutePath}/src/common${prefix}/resources"
            val baseFile = File(basePath)
            baseFile.walkBottomUp().filter { !it.isDirectory }.filterNot { it.extension == "png" }
                .filterNot { it.extension == "jpg" }.forEach {
                val encodeBase64 =
                    de.undercouch.gradle.tasks.download.org.apache.commons.codec.binary.Base64.encodeBase64(it.readBytes())
                val key = it.absolutePath.substring(baseFile.absolutePath.length + 1)
                val safeKey = key.replace("\$", "\\\$").replace("\\", "/")
                w.write("m[\"$safeKey\"] = \"${String(encodeBase64)}\"\n")
            }
            w.write("}\noverride fun get(key:String) = m[key]\n")
            w.write("override fun allResourceNames() = m.keys.sorted()\n}")
        }
    }

    /*
     * Define tasks
     */
    val jsWrapMainResources by registering {
        doFirst { wrapJsResources() }
    }
    val jsWrapTestResources by registering {
        doFirst { wrapJsResources(test = true) }
        dependsOn(jsWrapMainResources)
    }
    val jsCleanResources by creating {
        doFirst {
            File("${projectDir.absolutePath}/${customSrcDirs.jsTestGenerated}").deleteRecursively()
            File("${projectDir.absolutePath}/${customSrcDirs.jsMainGenerated}").deleteRecursively()
        }
    }

    /**
     * Task dependencies
     */
    val clean by getting { dependsOn(jsCleanResources) }
    val compileKotlinJs by getting { dependsOn(jsWrapMainResources) }
    val compileTestKotlinJs by getting { dependsOn(jsWrapTestResources) }

    /*
     * We need to "tweak" test tasks and their dependencies due do our custom targets
     */
    val jvmDataGenTest by getting { enabled = false }
    val compileTestKotlinJvmDataGen by getting { enabled = false }
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
