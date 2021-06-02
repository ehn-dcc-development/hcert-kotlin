import ehn.techiop.hcert.kotlin.chain.DecodeJsResult
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.Json as jsJson

@JsExport
@JsName("Verifier")
class Verifier(vararg val pemEncodedCertCertificates: String) {

    private val repo = PrefilledCertificateRepository(pemEncodedCertificates = pemEncodedCertCertificates)
    private val chain = DefaultChain.buildVerificationChain(repo)

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
        Verifier("foo").verify("bar")
    }
    console.info("EGC Verifier Loaded")
}