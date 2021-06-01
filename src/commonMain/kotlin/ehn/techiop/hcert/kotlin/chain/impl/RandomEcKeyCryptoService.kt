package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

expect class RandomEcKeyCryptoService constructor(
    keySize: Int = 256,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService
