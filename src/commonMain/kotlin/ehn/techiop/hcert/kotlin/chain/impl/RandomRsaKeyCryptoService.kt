package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.crypto.CryptoAdapter
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

class RandomRsaKeyCryptoService(
    keySize: Int = 2048,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : DefaultCryptoService(CryptoAdapter(KeyType.RSA, keySize, contentType, clock))