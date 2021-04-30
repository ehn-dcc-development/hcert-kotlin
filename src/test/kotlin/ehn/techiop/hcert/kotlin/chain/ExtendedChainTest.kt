@file:UseSerializers(serializerClasses = [InstantSerializer::class, X509CertificateSerializer::class, EudgcSerializer::class])

package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.data.EudgcSerializer
import ehn.techiop.hcert.kotlin.data.InstantSerializer
import ehn.techiop.hcert.kotlin.data.X509CertificateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Path
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ExtendedChainTest {

    @ParameterizedTest
    @MethodSource("inputProvider")
    fun success(input: TestInput) {
        val decisionService = DecisionService(Clock.fixed(input.verificationInstant, ZoneId.systemDefault()))
        val certificateRepository = PrefilledCertificateRepository(*input.trustedCertificates.toTypedArray())

        val verificationResult = VerificationResult()
        val decodingChain = Chain.buildVerificationChain(certificateRepository)
        val vaccinationData = decodingChain.decode(input.input, verificationResult)

        assertThat(vaccinationData, equalTo(input.expectedContent))
        assertThat(decisionService.decide(verificationResult), equalTo(input.expectedDecision))
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun inputProvider(): List<TestInput> {
            val testcaseFiles = listOf(
                "src/test/resources/testcase01.json"
            )
            return testcaseFiles.map { Json.decodeFromString(Files.readString(Path.of(it))) }
        }

    }

    @Serializable
    data class TestInput(
        val verificationInstant: Instant,
        val trustedCertificates: List<X509Certificate>,
        val input: String,
        val expectedContent: Eudgc,
        val expectedDecision: VerificationDecision
    )

}
