import ehn.techiop.hcert.kotlin.chain.*
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
        val root = PrefilledCertificateRepository(rootPem)
        val sig = trustListSignature.toByteArray()
        val content = trustListContent.toByteArray()
        repo = TrustListCertificateRepository(sig, content, root)
        chain = DefaultChain.buildVerificationChain(repo)
    }

    fun verify(qrContent: String): jsJson {
        val decodeResult = DecodeJsResult(chain.decode(qrContent))
        return JSON.parse(Json { encodeDefaults = true }.encodeToString(decodeResult))
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
        val directVerifier = Verifier("foo")
        directVerifier.verify("bar")
        val trustListVerifier = Verifier(
            "foo",
            ArrayBuffer.from("content".encodeToByteArray()),
            ArrayBuffer.from("signature".encodeToByteArray())
        )
        trustListVerifier.verify("bar")
        trustListVerifier.updateTrustList(
            "foo",
            ArrayBuffer.from("content".encodeToByteArray()),
            ArrayBuffer.from("signature".encodeToByteArray())
        )

        val generatorEcRandom = Generator(256)
        generatorEcRandom.encode(SampleData.vaccination)
        generatorEcRandom.encodeToQrCode(SampleData.vaccination, 3, 2)

        val generatorFixed = Generator("foo", "bar")
        generatorFixed.encode(SampleData.recovery)
        generatorFixed.encodeToQrCode(SampleData.recovery, 2, 1)
    }
    console.info("DCC Chain Loaded")
}
