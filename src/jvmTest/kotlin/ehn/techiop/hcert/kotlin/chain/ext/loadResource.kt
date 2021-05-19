package ehn.techiop.hcert.kotlin.chain.ext

import java.io.File

actual fun loadResource(filename: String) = File("src/commonTest/resources/$filename").readText() as String?
actual fun allResources(): Map<String, String> {
    val baseDir = File("src/commonTest/resources/dgc-testdata")
    return baseDir.walkTopDown()
        .filter { it.name.endsWith(".json") }
        .map { it.relativeTo(baseDir).path to it.readText() }
        .sortedBy { it.first }
        .toMap()
}