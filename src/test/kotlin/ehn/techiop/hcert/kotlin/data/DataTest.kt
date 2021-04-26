package ehn.techiop.hcert.kotlin.data

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.SampleData
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * Tests if our implementation (custom data class [GreenCertificate] and Kotlinx Serialization) matches
 * the data from the schema and Jackson Serializer
 */
class DataTest {

    @ParameterizedTest
    @MethodSource("stringProvider")
    fun decodeEncodeTest(input: String) {
        val dataOurs = Json { }.decodeFromString<GreenCertificate>(input)
        val dataTheirs = ObjectMapper().readValue(input, Eudgc::class.java)
        assertEquals(dataOurs, GreenCertificate.fromEuSchema(dataTheirs))

        val cborOur = Cbor { }.encodeToByteArray(dataOurs)
        //println(cborOur.toHexString())
        //val cborTheirs = CBORMapper().writeValueAsBytes(dataTheirs)
        //println(cborTheirs.toHexString())
        // will never be exactly the same ... because of default values and empty arrays
        // assertArrayEquals(cborOur, cborTheirs)

        val decodedFromCbor = Cbor { }.decodeFromByteArray<GreenCertificate>(cborOur)
        assertEquals(dataOurs, decodedFromCbor)
        assertEquals(GreenCertificate.fromEuSchema(dataTheirs), decodedFromCbor)
    }


    companion object {

        @JvmStatic
        @Suppress("unused")
        fun stringProvider() = listOf(
            SampleData.recovery,
            SampleData.testRat,
            SampleData.testNaa,
            SampleData.vaccination
        )

    }


}