package ehn.techiop.hcert.kotlin

import ehn.techiop.hcert.kotlin.chain.CryptoServiceHolder
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData

//RSA key generation can take ages in JS
//This location and this naming ensures that this executes before any other tests (although the Kotest docs say otherwise)
class A000MapFiller : FunSpec({
    withData(nameFn = { "prefill RandomCryptoService map for $it" }, ContentType.values().toList()) { ct ->

        CryptoServiceHolder.getRandomCryptoService(KeyType.EC, 256, ct)
        CryptoServiceHolder.getRandomCryptoService(KeyType.EC, 384, ct)

        CryptoServiceHolder.getRandomCryptoService(KeyType.RSA, 2048, ct)
        CryptoServiceHolder.getRandomCryptoService(KeyType.RSA, 3072, ct)

    }
})
