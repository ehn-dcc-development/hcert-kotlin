package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    @ExperimentalSerializationApi
    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)


    @ExperimentalSerializationApi
    override fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate =
        Cbor.decodeFromByteArray(input)
}