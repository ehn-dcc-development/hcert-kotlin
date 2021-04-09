package ehn.techiop.hcert.kotlin.chain


class CborProcessingChain(
    private val cborService: CborService,
    private val valSuiteService: ValSuiteService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    fun process(input: VaccinationData): ResultCbor {
        val cbor = cborService.encode(input)
        val cose = cborService.sign(cbor)
        val comCose = compressorService.encode(cose)
        val encodedComCose = base45Service.encode(comCose)
        val prefEncodedComCose = valSuiteService.encode(encodedComCose)
        return ResultCbor(cbor, cose, comCose, prefEncodedComCose)
    }

    fun verify(input: String): VaccinationData {
        val plainInput = valSuiteService.decode(input)
        val compressedCose = base45Service.decode(plainInput)
        val cose = compressorService.decode(compressedCose)
        val cbor = cborService.verify(cose)
        return cborService.decode(cbor)
    }

}

data class ResultCbor(
    val cbor: ByteArray,
    val cose: ByteArray,
    val compressedCose: ByteArray,
    val prefixedEncodedCompressedCose: String
)
