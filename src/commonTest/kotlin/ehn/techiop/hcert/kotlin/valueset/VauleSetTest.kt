package ehn.techiop.hcert.kotlin.valueset

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

class VauleSetTest : StringSpec({

    "V1 Client-Server Exchange" {
        val cryptoService = RandomEcKeyCryptoService()
        val certificate = cryptoService.getCertificate().encoded.asBase64()
        val encodeService = ValueSetV1EncodeService(cryptoService)
        val valueSetJson = """
            {
              "valueSetId": "disease-agent-targeted",
              "valueSetDate": "2021-04-27",
              "valueSetValues": {
                "840539006": {
                  "display": "COVID-19",
                  "lang": "en",
                  "active": true,
                  "version": "http://snomed.info/sct/900000000000207008/version/20210131",
                  "system": "http://snomed.info/sct"
                }
              }
            }
        """.trimIndent()
        val signedData = encodeService.encode(listOf(ValueSet("disease-agent-targeted", valueSetJson)))

        verifyClientOperations(certificate, signedData, valueSetJson)
    }


})

private fun verifyClientOperations(
    certificate: String,
    signedData: SignedData,
    valueSetJson: String
) {
    val clientTrustRoot = PrefilledCertificateRepository(certificate)
    val decodeService = ValueSetDecodeService(clientTrustRoot)
    val (clientSignedDataParsed, valueSets) = decodeService.decode(signedData)

    valueSets.valueSets.size shouldBe 1
    for (vs in valueSets.valueSets) {
        Json.parseToJsonElement(vs.valueSet) shouldBe Json.parseToJsonElement(valueSetJson)
    }
    clientSignedDataParsed.validFrom.epochSeconds shouldBeLessThanOrEqual Clock.System.now().epochSeconds
    clientSignedDataParsed.validUntil.epochSeconds shouldBeGreaterThanOrEqual Clock.System.now().epochSeconds
    clientSignedDataParsed.content shouldBe signedData.content
}