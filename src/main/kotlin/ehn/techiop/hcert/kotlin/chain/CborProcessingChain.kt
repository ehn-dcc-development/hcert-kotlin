package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray


class CborProcessingChain(
    private val cborService: CborService,
    private val valSuiteService: ValSuiteService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    fun process(input: VaccinationData): ResultCbor {
        val cbor = Cbor { ignoreUnknownKeys = true }.encodeToByteArray(input)
        val cose = cborService.sign(cbor)
        val comCose = compressorService.encode(cose)
        val encodedComCose = base45Service.encode(comCose)
        val prefEncodedComCose = valSuiteService.encode(encodedComCose)
        return ResultCbor(cbor, cose, comCose, prefEncodedComCose)
    }

    fun verify(input: String, verificationResult: VerificationResult = VerificationResult()): VaccinationData {
        val plainInput = valSuiteService.decode(input, verificationResult)
        val compressedCose = base45Service.decode(plainInput, verificationResult)
        val cose = compressorService.decode(compressedCose, verificationResult)
        val cbor = cborService.verify(cose, verificationResult)
        return Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<VaccinationData>(cbor).also {
            verificationResult.success = true
        }
    }

}

class VerificationResult {
    var base45Decoded = false
    var valSuitePrefix: String? = null
    var zlibDecoded = false
    var coseSignatureVerified = false
    var success = false
}

data class ResultCbor(
    val cbor: ByteArray,
    val cose: ByteArray,
    val compressedCose: ByteArray,
    val prefixedEncodedCompressedCose: String
)
