import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlin.js.Json as jsJson

@JsExport
@JsName("Verifier")
class Verifier(vararg val pemEncodedCertCertificates: String) {
    private val repo = PrefilledCertificateRepository(pemEncodedCertificates = pemEncodedCertCertificates)
    private val chain = DefaultChain.buildVerificationChain(repo)

    private val decisionService = DecisionService()

    fun verify(qrContent: String): jsJson = JSON.parse(Json.encodeToString(chain.decode(qrContent)))

    fun decide(verificationResult: JSON) = decisionService.decide(Json.decodeFromDynamic(verificationResult)).name
}