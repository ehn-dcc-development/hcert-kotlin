package ehn.techiop.hcert.kotlin.chain.ext

import TestResourceHolder
import ehn.techiop.hcert.kotlin.data.loadAsString


actual fun allOfficialTestCases(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    TestResourceHolder.allResourceNames()
        .filter { it.startsWith("dgc-testdata/") }
        .filter { it.endsWith(".json") }
        .forEach { map[it] = TestResourceHolder.loadAsString(it)!! }
    return map
}
