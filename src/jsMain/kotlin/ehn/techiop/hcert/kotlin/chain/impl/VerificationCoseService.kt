package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.trust.buildCosePublicKey
import org.khronos.webgl.Uint8Array

actual class VerificationCoseService actual constructor(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        verificationResult.coseVerified = false
        val cborJson = Cbor.decode(input)
        val protectedHeader = cborJson["value"][0]
        val unprotectedHeader = cborJson["value"][1]
        val content = cborJson["value"][2]
        val signature = cborJson["value"][3]
        val protectedHeaderCbor = Cbor.decode(protectedHeader)
        val kid = protectedHeaderCbor.get(4) as Uint8Array? ?: if (unprotectedHeader.length !== undefined)
            Cbor.decode(unprotectedHeader).get(1) as Uint8Array
        else
            throw IllegalArgumentException("KID not found")
        if (kid === undefined)
            throw IllegalArgumentException("KID not found")
        val algorithm = protectedHeaderCbor.get(1)
        repository.loadTrustedCertificates(kid.toByteArray(), verificationResult).forEach { trustedCert ->
            verificationResult.certificateValidFrom = trustedCert.validFrom
            verificationResult.certificateValidUntil = trustedCert.validUntil
            verificationResult.certificateValidContent = trustedCert.validContentTypes
            Cose.verify(input, trustedCert.buildCosePublicKey()).also {
                // TODO make this a suspend function, and then provide a wrapper from JS to call it as a promise
                it.then { verificationResult.coseVerified = true }
                return@forEach
            }
        }
        return content
    }

}