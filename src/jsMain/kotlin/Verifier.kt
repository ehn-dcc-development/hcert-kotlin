import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.DecodeJsResult
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.from
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import ehn.techiop.hcert.kotlin.chain.toByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.khronos.webgl.ArrayBuffer
import kotlin.js.Json as jsJson

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

    }
    console.info("EGC Verifier Loaded")
}
