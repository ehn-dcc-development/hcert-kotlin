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

    val cborJson = Cbor.Decoder.decodeAllSync(Buffer(augmentedInput(input).toUint8Array()))
    val cose = cborJson[0] as Cbor.Tagged

    @Suppress("UNCHECKED_CAST")
    val coseValue = cose.value as Array<Buffer>
    val protectedHeader = coseValue[0]
    val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()
    val unprotectedHeader = coseValue[1].asDynamic()
    val content = coseValue[2]
    val signature = coseValue[3]

    actual fun getProtectedAttributeByteArray(key: Int) =
        (protectedHeaderCbor?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getUnprotectedAttributeByteArray(key: Int) =
        (unprotectedHeader?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getProtectedAttributeInt(key: Int) =
        protectedHeaderCbor?.get(key) as Int?

    actual fun validate(kid: ByteArray, repository: CertificateRepository): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            val result = jsTry {
                Cose.verifySync(input, it.publicKey) !== undefined
            }.catch {
                false
            }
            if (result) return true // else try next
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        repository: CertificateRepository,
        verificationResult: VerificationResult
    ): Boolean {
        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
            verificationResult.setCertificateData(trustedCert)
            val result = jsTry {
                Cose.verifySync(augmentedInput(input), trustedCert.publicKey) !== undefined
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
        verificationResult.setCertificateData(cryptoService.getCertificate())
        val pubKey = cryptoService.getCborVerificationKey(kid, verificationResult)
        return jsTry {
            Cose.verifySync(augmentedInput(input), pubKey) !== undefined
        }.catch {
            false
        }
    }

    actual fun getContent() = content.toByteArray()

    actual fun getContentMap() = CwtAdapter(content.toByteArray())

    /**
     * Input may be missing COSE Tag 0xD2 = 18 = cose-sign1.
     * But we need this, to cast the parsed object to [Cbor.Tagged].
     * So we'll add the Tag to the input.
     *
     * It may also be tagged as a CWT (0xD8, 0x3D) and a Sign1 (0xD2).
     * But the library expects only one tag.
     * So we'll strip the CWT tag from the input.
     */
    private fun augmentedInput(input: ByteArray): ByteArray {
        if (input.size >= 1 && isArray(input[0]))
            return byteArrayOf(0xD2.toByte()) + input
        if (input.size >= 3 && isCwt(input[0], input[1]) && isSign1(input[2])) {
            return input.drop(2).toByteArray()
        }
        return input
    }

    private fun isSign1(byte: Byte) = byte == 0xD2.toByte()

    private fun isCwt(firstByte: Byte, secondByte: Byte) = firstByte == 0xD8.toByte() && secondByte == 0x3D.toByte()

    private fun isArray(byte: Byte) = byte == 0x84.toByte()

}