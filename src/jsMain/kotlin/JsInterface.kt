import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.log.BasicLogger
import ehn.techiop.hcert.kotlin.log.JsLogger
import ehn.techiop.hcert.kotlin.rules.BusinessRulesDecodeService
import ehn.techiop.hcert.kotlin.trust.SignedData
import ehn.techiop.hcert.kotlin.trust.TrustListDecodeService
import ehn.techiop.hcert.kotlin.valueset.ValueSetDecodeService
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.khronos.webgl.ArrayBuffer
import kotlin.js.Json as jsJson


@JsExport
@JsName("defaultLogger")
private val defaultLogger = BasicLogger()

@JsExport
@JsName("addLogger")
@Suppress("NON_EXPORTABLE_TYPE")
fun addLogger(logger: Antilog) = Napier.base(logger)

@JsExport
@JsName("removeLogger")
@Suppress("NON_EXPORTABLE_TYPE")
fun removeLogger(logger: Antilog) = Napier.takeLogarithm(logger)

@JsExport
@JsName("setLogLevel")
fun setLogLevel(level: String?) {
    ehn.techiop.hcert.kotlin.log.setLogLevel(Napier.Level.values().firstOrNull { it.name == level?.uppercase() })
}

@JsExport
@JsName("setLogger")
fun setLogger(
    loggingFunction: (level: String, tag: String?, stackTrace: String?, message: String?) -> Unit,
    keep: Boolean? = false
): dynamic {
    if (keep == null || keep === undefined || !keep) Napier.takeLogarithm()
    return JsLogger(loggingFunction).also { Napier.base(it) }
}


@JsExport
@JsName("Verifier")
class Verifier {

    private val jsonEncoder = Json { encodeDefaults = true }
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
            val contentAndSig = SignedData(content, sig)
            repo = TrustListCertificateRepository(contentAndSig, root)
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
        val extResult = chain.decode(qrContent)
        val decodeResult = DecodeResultJs(
            extResult.verificationResult.error == null,
            extResult.verificationResult.error?.name,
            VerificationResultJs(extResult.verificationResult),
            extResult.chainDecodeResult.eudgc
        )
        return JSON.parse(jsonEncoder.encodeToString(decodeResult))
    }

    /**
     * We'll make sure, that [DecodeResultJs] contains only
     * types that export nicely to JavaScript, so it's okay
     * to suppress the warning.
     */
    @Suppress("NON_EXPORTABLE_TYPE")
    fun verifyDataClass(qrContent: String): DecodeResultJs {
        return DecodeResultJs(chain.decode(qrContent))
    }

}

@JsExport
@JsName("Generator")
class Generator {

    private val jsonEncoder = Json { encodeDefaults = true }
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
        return JSON.parse(jsonEncoder.encodeToString(encodeResult))
    }

    fun encodeToQrCode(input: String, moduleSize: Int, marginSize: Int): String {
        val encodeResult = chain.encode(Json.decodeFromString(input))
        val encode = DefaultTwoDimCodeService(moduleSize, marginSize).encode(encodeResult.step5Prefixed)
        return "data:image/gif;base64,${encode.asBase64()}"
    }
}

@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
@JsName("SignedDataDownloader")
object SignedDataDownloader {

    @JsName("loadTrustList")
    fun loadTrustList(rootPem: String, content: ArrayBuffer, signature: ArrayBuffer) = jsTry {
        val root = PrefilledCertificateRepository(rootPem)
        val contentAndSig = SignedData(content.toByteArray(), signature.toByteArray())
        TrustListDecodeService(root).decode(contentAndSig).also {
            it.first.replaceDatesWithJsTypes()
        }
    }.catch {
        if (it is VerificationException)
            throw it
        throw VerificationException(Error.TRUST_SERVICE_ERROR, it.message, it)
    }

    @JsName("loadBusinessRules")
    fun loadBusinessRules(rootPem: String, content: ArrayBuffer, signature: ArrayBuffer) = jsTry {
        val root = PrefilledCertificateRepository(rootPem)
        val contentAndSig = SignedData(content.toByteArray(), signature.toByteArray())
        BusinessRulesDecodeService(root).decode(contentAndSig).also {
            it.first.replaceDatesWithJsTypes()
        }
    }.catch {
        if (it is VerificationException)
            throw it
        throw VerificationException(Error.TRUST_SERVICE_ERROR, it.message, it)
    }

    @JsName("loadValueSets")
    fun loadValueSets(rootPem: String, content: ArrayBuffer, signature: ArrayBuffer) = jsTry {
        val root = PrefilledCertificateRepository(rootPem)
        val contentAndSig = SignedData(content.toByteArray(), signature.toByteArray())
        ValueSetDecodeService(root).decode(contentAndSig).also {
            it.first.replaceDatesWithJsTypes()
        }
    }.catch {
        if (it is VerificationException)
            throw it
        throw VerificationException(Error.TRUST_SERVICE_ERROR, it.message, it)
    }

}

/**
 * Expose some functions to be called from regular JavaScript
 *
 * Needs to be in a "main" method:
 * https://stackoverflow.com/questions/60183300/how-to-call-kotlin-js-functions-from-regular-javascript#comment106601781_60184178
 */
fun main() {
    //is NOOP by default because log level is null by default
    Napier.base(defaultLogger)
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

        SignedDataDownloader.loadBusinessRules("", ArrayBuffer(0), ArrayBuffer(0))

        val generatorEcRandom = Generator(256)
        generatorEcRandom.encode("bar")
        generatorEcRandom.encodeToQrCode("bar", 3, 2)

        val generatorFixed = Generator("bar", "bar")
        generatorFixed.encode("bar")
        generatorFixed.encodeToQrCode("bar", 2, 1)
    }
    console.info("DCC Chain Loaded")
}
