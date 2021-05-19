package ehn.techiop.hcert.kotlin.chain.ext

import RHolder
import ehn.techiop.hcert.kotlin.chain.fromBase64

actual fun allResources() =
    RHolder.m.map { it.key to it.value.fromBase64().decodeToString() }.sortedBy { it.first }.toMap()