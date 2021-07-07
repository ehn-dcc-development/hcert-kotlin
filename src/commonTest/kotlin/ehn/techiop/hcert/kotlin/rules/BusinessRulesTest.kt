package ehn.techiop.hcert.kotlin.rules

import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.trust.SignedData
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class BusinessRulesTest : StringSpec({

    "V1 Client-Server Exchange" {
        val cryptoService = RandomEcKeyCryptoService()
        val certificate = cryptoService.getCertificate().encoded.asBase64()
        val encodeService = BusinessRulesV1EncodeService(cryptoService)
        val ruleJson =
            "{\"Identifier\":\"GR-AT-0000\",\"Type\":\"Acceptance\",\"Country\":\"AT\",\"Region\":\"AT\",\"Version\":\"1.0.0\",\"SchemaVersion\":\"1.0.0\",\"Engine\":\"CERTLOGIC\",\"EngineVersion\":\"0.7.5\",\"CertificateType\":\"General\",\"Description\":[{\"lang\":\"en\",\"desc\":\"Exactly one type of event.\"}],\"ValidFrom\":\"2021-07-05T00:00:00Z\",\"ValidTo\":\"2030-06-01T00:00:00Z\",\"AffectedFields\":[\"r\",\"t\",\"v\"],\"Logic\":{\"===\":[{\"reduce\":[[{\"var\":\"payload.r\"},{\"var\":\"payload.t\"},{\"var\":\"payload.v\"}],{\"+\":[{\"var\":\"accumulator\"},{\"if\":[{\"var\":\"current.0\"},1,0]}]},0]},1]}}"
        val signedData = encodeService.encode(listOf(BusinessRule(ruleJson)))

        verifyClientOperations(certificate, signedData, ruleJson)
    }


})

private fun verifyClientOperations(
    certificate: String,
    signedData: SignedData,
    ruleJson: String
) {
    val clientTrustRoot = PrefilledCertificateRepository(certificate)
    val decodeService = BusinessRulesDecodeService(clientTrustRoot)
    val (clientSignedDataParsed, clientRules) = decodeService.decode(signedData)

    clientRules.rules.size shouldBe 1
    for (clientRule in clientRules.rules) {
        Json.parseToJsonElement(clientRule.rule) shouldBe Json.parseToJsonElement(ruleJson)
    }
    clientSignedDataParsed.validFrom.epochSeconds shouldBeLessThanOrEqual Clock.System.now().epochSeconds
    clientSignedDataParsed.validUntil.epochSeconds shouldBeGreaterThanOrEqual Clock.System.now().epochSeconds
    clientSignedDataParsed.content shouldBe signedData.content
}