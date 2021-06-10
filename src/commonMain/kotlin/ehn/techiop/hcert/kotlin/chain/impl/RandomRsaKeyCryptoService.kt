package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

expect class RandomRsaKeyCryptoService constructor(
    keySize: Int = 2048,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService
