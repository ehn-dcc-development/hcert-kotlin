package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.crypto.CryptoAdapter
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import kotlin.jvm.JvmOverloads

class RandomEcKeyCryptoService @JvmOverloads constructor(
    keySize: Int = 256,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : DefaultCryptoService(CryptoAdapter(KeyType.EC, keySize, contentType, clock))