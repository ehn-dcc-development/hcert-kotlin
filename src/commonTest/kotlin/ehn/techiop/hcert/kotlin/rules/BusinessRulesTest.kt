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
        val ruleJson = """
            {
              "Identifier": "GR-DX-0001",
              "Type": "Acceptance",
              "Country": "DX",
              "Version": "1.0.0",
              "SchemaVersion": "1.0.0",
              "Engine": "CERTLOGIC",
              "EngineVersion": "2.0.1",
              "CertificateType": "General",
              "Description": [
                {
                  "lang": "en",
                  "desc": "api-test-rule for use in api test"
                }
              ],
              "ValidFrom": "2021-07-01T09:38:09+02:00",
              "ValidTo": "2021-07-06T09:38:09+02:00",
              "AffectedFields": [
                "dt",
                "nm"
              ],
              "Logic": {
                "and": [
                  {
                    ">=": [
                      {
                        "var": "dt"
                      },
                      "23.12.2012"
                    ]
                  },
                  {
                    ">=": [
                      {
                        "var": "nm"
                      },
                      "ABC"
                    ]
                  }
                ]
              }
            }
        """.trimIndent()
        val signedData = encodeService.encode(listOf(BusinessRule("GR-DX-0001", ruleJson)))

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