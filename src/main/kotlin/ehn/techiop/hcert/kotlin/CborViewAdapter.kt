package ehn.techiop.hcert.kotlin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CborViewAdapter(
    private val cborProcessingChain: CborProcessingChain,
    private val base45Service: Base45Service,
    private val qrCodeService: TwoDimCodeService,
    private val aztecService: TwoDimCodeService
) {

    fun process(input: String): CardViewModel {
        val result = cborProcessingChain.process(Json { isLenient = true }.decodeFromString(input))
        val qrCode = qrCodeService.encode(result.prefixedEncodedCompressedCose)
        val aztecCode = aztecService.encode(result.prefixedEncodedCompressedCose)
        return CardViewModel(
            "COSE",
            base64Items = listOf(
                Base64Item("CBOR (Base45)", base45Service.encode(result.cbor)),
                Base64Item("CBOR (Hex)", result.cbor.toHexString()),
                Base64Item("COSE (Base45)", base45Service.encode(result.cose)),
                Base64Item("COSE (Hex)", result.cose.toHexString()),
                Base64Item("Compressed COSE (Base45)", base45Service.encode(result.compressedCose)),
                Base64Item("Compressed COSE (Hex)", result.compressedCose.toHexString()),
                Base64Item("Prefixed Compressed COSE", result.prefixedEncodedCompressedCose)
            ),
            codeResources = listOf(
                CodeResource("QR code based on prefixed compressed COSE", qrCode),
                CodeResource("Aztec code based on prefixed compressed COSE", aztecCode)
            )
        )
    }

}

private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }