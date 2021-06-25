import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.impl.*
import io.github.aakira.napier.Napier
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.khronos.webgl.ArrayBuffer
import kotlin.js.Json as jsJson

@JsExport
@JsName("setLogLevel")
fun setLogLevel(level: String?) =
    ehn.techiop.hcert.kotlin.log.setLogLevel(Napier.Level.values().firstOrNull { it.name == level?.uppercase() })

@JsExport
@JsName("Verifier")
class Verifier {

    private lateinit var repo: CertificateRepository
    private lateinit var chain: Chain

    @JsName("VerifierDirect")
    constructor(vararg pemEncodedCertCertificates: String) {
        repo = PrefilledCertificateRepository(pemEncodedCertificates = pemEncodedCertCertificates)
        chain = DefaultChain.buildVerificationChain(repo)
    }

    @JsName("VerifierTrustList")
    constructor(rootPem: String, trustListContent: ArrayBuffer, trustListSignature: ArrayBuffer) {
        updateTrustList(rootPem, trustListContent, trustListSignature)
    }

    @JsName("updateTrustList")
    fun updateTrustList(rootPem: String, trustListContent: ArrayBuffer, trustListSignature: ArrayBuffer) {
        jsTry {
            val root = PrefilledCertificateRepository(rootPem)
            val sig = trustListSignature.toByteArray()
            val content = trustListContent.toByteArray()
            repo = TrustListCertificateRepository(sig, content, root)
            chain = DefaultChain.buildVerificationChain(repo)
        }.catch {
            if (it is VerificationException)
                throw it
            throw VerificationException(Error.TRUST_SERVICE_ERROR, it.message, it)
        }
    }

    /**
     * Returns a serialization of [DecodeResultJs]
     */
    fun verify(qrContent: String): jsJson {
        val decodeResult = DecodeResultJs(chain.decode(qrContent))
        return JSON.parse(Json {
            encodeDefaults = true
        }.encodeToString(decodeResult.also { it.greenCertificate?.kotlinify() }))
    }

    /**
     * We'll make sure, that [DecodeResultJs] contains only
     * types that export nicely to JavaScript, so it's okay
     * to suppress the warning.ü0ü0
     */
    @Suppress("NON_EXPORTABLE_TYPE")
    fun verifyDataClass(qrContent: String): DecodeResultJs {
        return DecodeResultJs(chain.decode(qrContent))
    }

}

@JsExport
@JsName("Generator")
class Generator {

    private val cryptoService: CryptoService
    private val chain: Chain

    @JsName("GeneratorEcRandom")
    constructor(keySize: Int = 256) {
        cryptoService = RandomEcKeyCryptoService(keySize)
        chain = DefaultChain.buildCreationChain(cryptoService)
    }

    @JsName("GeneratorFixed")
    constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {
        cryptoService = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        chain = DefaultChain.buildCreationChain(cryptoService)
    }

    fun encode(input: String): jsJson {
        val encodeResult = chain.encode(Json.decodeFromString(input))
        return JSON.parse(Json { encodeDefaults = true }.encodeToString(encodeResult))
    }

    fun encodeToQrCode(input: String, moduleSize: Int, marginSize: Int): String {
        val encodeResult = chain.encode(Json.decodeFromString(input))
        val encode = DefaultTwoDimCodeService(moduleSize, marginSize).encode(encodeResult.step5Prefixed)
        return "data:image/gif;base64,${encode.asBase64()}"
    }
}

/**
 * Expose some functions to be called from regular JavaScript
 *
 * Needs to be in a "main" method:
 * https://stackoverflow.com/questions/60183300/how-to-call-kotlin-js-functions-from-regular-javascript#comment106601781_60184178
 */
fun main() {
    if (false) {
        val directVerifier = Verifier("bar")
        directVerifier.verify("bar")
        directVerifier.verifyDataClass("bar")
        val trustListVerifier = Verifier(
            "bar",
            ArrayBuffer.from("content".encodeToByteArray()),
            ArrayBuffer.from("signature".encodeToByteArray())
        )
        trustListVerifier.verify("bar")
        trustListVerifier.verifyDataClass("bar")
        trustListVerifier.updateTrustList(
            "bar",
            ArrayBuffer.from("content".encodeToByteArray()),
            ArrayBuffer.from("signature".encodeToByteArray())
        )

        val generatorEcRandom = Generator(256)
        generatorEcRandom.encode("bar")
        generatorEcRandom.encodeToQrCode("bar", 3, 2)

        val generatorFixed = Generator("bar", "bar")
        generatorFixed.encode("bar")
        generatorFixed.encodeToQrCode("bar", 2, 1)
    }
    console.info("DCC Chain Loaded")
}
