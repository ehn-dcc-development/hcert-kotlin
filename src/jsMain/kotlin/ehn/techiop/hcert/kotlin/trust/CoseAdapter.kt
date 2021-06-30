package ehn.techiop.hcert.kotlin.trust

import Buffer
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.impl.CborHelper
import ehn.techiop.hcert.kotlin.crypto.Cose
import io.github.aakira.napier.Napier
import org.khronos.webgl.Uint8Array

actual class CoseAdapter actual constructor(private val input: ByteArray) {


    //work around inherent limitations of jsTry-Wrapper
    private val parsed = parse(input)
    val cborJson = parsed.cborJson
    val cose = parsed.cose
    val coseValue = parsed.coseValue
    val protectedHeader = parsed.protectedHeader
    val protectedHeaderCbor = parsed.protectedHeaderCbor
    val unprotectedHeader = parsed.unprotectedHeader
    val content = parsed.content
    val signature = parsed.signature

    companion object {
        private val debugTag = CoseAdapter::class.simpleName + hashCode().toString()

        private data class InitHelper(
            val cborJson: Array<Any>,
            val cose: Cbor.Tagged,
            val coseValue: Array<Buffer>,
            val protectedHeader: Buffer,
            val protectedHeaderCbor: dynamic,
            val unprotectedHeader: dynamic,
            val content: Buffer,
            val signature: Buffer
        )

        private fun parse(input: ByteArray) =
            jsTry {
                Napier.d(tag = debugTag, message = "CBOR-decoding input")
                val cborJson = CborHelper.decodeAll(augmentedInput(input))
                Napier.v(tag = debugTag, message = "CBOR-decoded input is\n${JSON.stringify(cborJson, null, 2)}")
                Napier.d(tag = debugTag, message = "extracting tagged element")
                val cose = cborJson[0] as Cbor.Tagged

                @Suppress("UNCHECKED_CAST")
                val coseValue = cose.value as Array<Buffer>
                val protectedHeader = coseValue[0]

                Napier.d(tag = debugTag, message = "decoding protected header")
                val protectedHeaderCbor = CborHelper.decodeFirst(protectedHeader)
                Napier.d(
                    tag = debugTag,
                    message = "decoded protected header is\n${JSON.stringify(protectedHeaderCbor, null, 2)}"
                )
                Napier.d(tag = debugTag, message = "extracting unprotected header")
                val unprotectedHeader = coseValue[1].asDynamic()
                val content = coseValue[2]
                val signature = coseValue[3]
                InitHelper(
                    cborJson,
                    cose,
                    coseValue,
                    protectedHeader,
                    protectedHeaderCbor,
                    unprotectedHeader,
                    content,
                    signature
                )
            }.catch {
                throw VerificationException(Error.SIGNATURE_INVALID, cause = it)
            }


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


    actual fun getProtectedAttributeByteArray(key: Int) =
        (protectedHeaderCbor?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getUnprotectedAttributeByteArray(key: Int) =
        (unprotectedHeader?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getProtectedAttributeInt(key: Int) =
        protectedHeaderCbor?.get(key) as Int?

    actual fun validate(kid: ByteArray, repository: CertificateRepository) =
        validate(kid, repository, VerificationResult())

    actual fun validate(
        kid: ByteArray,
        repository: CertificateRepository,
        verificationResult: VerificationResult
    ): Boolean {
        repository.loadTrustedCertificates(kid, verificationResult).forEach {
            val result = jsTry {
                Cose.verifySync(augmentedInput(input), it.publicKey) !== undefined
            }.catch {
                false
            }
            if (result) {
                verificationResult.setCertificateData(it)
                return true
            } // else try next
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        cryptoService: CryptoService,
        verificationResult: VerificationResult
    ): Boolean {
        val pubKey = cryptoService.getCborVerificationKey(kid, verificationResult)
        val result = jsTry {
            Cose.verifySync(augmentedInput(input), pubKey) !== undefined
        }.catch {
            false
        }
        if (result) {
            verificationResult.setCertificateData(cryptoService.getCertificate())
        }
        return result
    }

    actual fun getContent() = content.toByteArray()

    actual fun getContentMap() = CwtHelper.fromCbor(content.toByteArray())


}
