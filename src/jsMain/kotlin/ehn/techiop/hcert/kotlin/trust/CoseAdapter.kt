package ehn.techiop.hcert.kotlin.trust

import Buffer
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.crypto.Cose
import org.khronos.webgl.Uint8Array

actual class CoseAdapter actual constructor(private val input: ByteArray) {
    val cborJson = Cbor.Decoder.decodeAllSync(Buffer(input.toUint8Array()))
    val cose = cborJson[0] as Cbor.Tagged
    val coseValue = cose.value as Array<Buffer>
    val protectedHeader = coseValue[0]
    val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()
    val unprotectedHeader = coseValue[1].asDynamic()
    val content = coseValue[2]
    val signature = coseValue[3]
    val cwtMap = Cbor.Decoder.decodeAllSync(Buffer.Companion.from(content.toByteArray().toUint8Array()))[0].asDynamic()

    actual fun getProtectedAttributeByteArray(key: Int) =
        (protectedHeaderCbor?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getUnprotectedAttributeByteArray(key: Int) =
        (unprotectedHeader?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getProtectedAttributeInt(key: Int) =
        protectedHeaderCbor?.get(key) as Int?

    actual fun validate(kid: ByteArray, repository: CertificateRepository): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            try {
                val result = Cose.verifySync(input, it.cosePublicKey)
                if (result !== undefined) return true
            } catch (ignored: dynamic) {
            }
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        repository: CertificateRepository,
        verificationResult: VerificationResult
    ): Boolean {
        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
            verificationResult.certificateValidFrom = trustedCert.validFrom
            verificationResult.certificateValidUntil = trustedCert.validUntil
            verificationResult.certificateValidContent = trustedCert.validContentTypes
            val result = jsTry {
                val result = Cose.verifySync(input, trustedCert.cosePublicKey) !== undefined
                verificationResult.coseVerified = result
                return@jsTry result
            }.catch {
                false
            }
            if (result) return true // else try next
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        cryptoService: CryptoService,
        verificationResult: VerificationResult
    ): Boolean {
        val pubKey = cryptoService.getCborVerificationKey(kid, verificationResult)
        val result = Cose.verifySync(input, pubKey)
        return jsTry {
            val result = Cose.verifySync(input, pubKey) !== undefined
            verificationResult.coseVerified = result
            return@jsTry result
        }.catch {
            false
        }
    }

    actual fun getContent() = content.toByteArray()

    actual fun getMapEntryByteArray(value: Int) = (cwtMap?.get(value) as Uint8Array?)?.toByteArray()

    actual fun getMapEntryNumber(value: Int) = cwtMap?.get(value) as Number?
}