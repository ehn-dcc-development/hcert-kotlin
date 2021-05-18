package ehn.techiop.hcert.kotlin.chain.ext

actual fun loadResource(filename: String) = String::class.java.getResource(filename)?.readText()