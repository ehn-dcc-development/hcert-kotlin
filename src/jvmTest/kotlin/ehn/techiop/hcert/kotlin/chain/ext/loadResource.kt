package ehn.techiop.hcert.kotlin.chain.ext

import java.io.File

actual fun loadResource(filename: String) = File("src/commonTest/resources/$filename").readText() as String?