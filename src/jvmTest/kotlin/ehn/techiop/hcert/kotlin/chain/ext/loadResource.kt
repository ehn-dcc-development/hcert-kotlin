package ehn.techiop.hcert.kotlin.chain.ext

import java.io.File

actual fun allOfficialTestCases(): Map<String, String> {
    val baseDir = File("src/commonTest/resources/dgc-testdata")
    return baseDir.walkTopDown()
        .filter { it.name.endsWith(".json") }
        .map { it.relativeTo(baseDir).path to it.readText() }
        .sortedBy { it.first }
        .toMap()
}