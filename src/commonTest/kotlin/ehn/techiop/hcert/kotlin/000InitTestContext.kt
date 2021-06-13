package ehn.techiop.hcert.kotlin

import ehn.techiop.hcert.kotlin.chain.CryptoServiceHolder
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.log.antilog
import ehn.techiop.hcert.kotlin.log.setLogLevel
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.github.aakira.napier.Napier
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData

//RSA key generation can take ages in JS
//This location and this naming ensures that this executes before any other tests (although the Kotest docs say otherwise)
class A000InitTestContext : FunSpec({

    test("Enabling logging") {
        setLogLevel(Napier.Level.VERBOSE)

        //This is pretty awesome, as it supports an arbitrary number of arbitrary loggers
        //So we could create a custom test logger, which collects all log messages their parameters
        //This would allows us to write tests, which make sure that both JS and JVM target provide the same level of details (i.e. meaningful stack traces for exceptions, which are currently heavily platform-specific)
        Napier.base(antilog())
        Napier.i(message = "Logging enabled, you should see me!", tag = "Test Context")
    }

    withData(nameFn = { "prefill RandomCryptoService map for $it" }, ContentType.values().toList()) { ct ->


        CryptoServiceHolder.getRandomCryptoService(KeyType.EC, 256, ct)
        CryptoServiceHolder.getRandomCryptoService(KeyType.EC, 384, ct)

        Napier.i(tag = "start", message = "Generating RSA 2048 key for $ct")
        CryptoServiceHolder.getRandomCryptoService(KeyType.RSA, 2048, ct)
        Napier.i(tag = "done", message = "Generating RSA 2048 key for $ct")
        Napier.i(tag = "start", message = "Generating RSA 3072 key for $ct")
        CryptoServiceHolder.getRandomCryptoService(KeyType.RSA, 3072, ct)
        Napier.i(tag = "done", message = "Generating RSA 3072 key for $ct")

    }
})
