package ehn.techiop.hcert.kotlin.chain.faults

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.VaccinationData
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

/**
 * Encodes the input without the required structure around it
 */
class FaultyCborService : DefaultCborService() {

    override fun encode(input: VaccinationData): ByteArray {
        val cbor = Cbor { ignoreUnknownKeys = true }.encodeToByteArray(input)
        return CBORObject.FromObject(cbor).EncodeToBytes()
    }

}