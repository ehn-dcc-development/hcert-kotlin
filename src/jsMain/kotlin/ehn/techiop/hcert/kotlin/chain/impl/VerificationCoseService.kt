package ehn.techiop.hcert.kotlin.chain.impl

import Buffer
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.crypto.Cose
import org.khronos.webgl.Uint8Array

actual class VerificationCoseService actual constructor(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        try {
            val cborJson = Cbor.Decoder.decodeAllSync(Buffer.from(input.toUint8Array()))
            val cwt = cborJson[0] as Cbor.Tagged
            val cwtValue = cwt.value as Array<Buffer>
            val protectedHeader = cwtValue[0]
            val unprotectedHeader = cwtValue[1].asDynamic()
            val content = cwtValue[2]
            val signature = cwtValue[3]

            val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()
            val kid = protectedHeaderCbor?.get(4) as Uint8Array? ?: unprotectedHeader?.get(4) as Uint8Array

            if (kid === undefined) throw IllegalArgumentException("KID not found")

            val algorithm = protectedHeaderCbor?.get(1) ?: unprotectedHeader?.get(1)

            repository.loadTrustedCertificates(kid.toByteArray(), verificationResult).forEach { trustedCert ->
                verificationResult.certificateValidFrom = trustedCert.validFrom
                verificationResult.certificateValidUntil = trustedCert.validUntil
                verificationResult.certificateValidContent = trustedCert.validContentTypes
                val pubKey = trustedCert.cosePublicKey
                val result = Cose.verify(input, pubKey)
                verificationResult.coseVerified = true
                return@forEach
            }

            return content.toByteArray()
        } catch (e: dynamic) {
            return input
        }
    }
}
