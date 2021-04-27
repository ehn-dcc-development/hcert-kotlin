package ehn.techiop.hcert.kotlin.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.SampleData
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
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
        assertThat(dataOurs, equalTo(GreenCertificate.fromEuSchema(dataTheirs)))

        // will never be exactly the same ... because Kotlin serializes lists
        // in CBOR as indefinite-length, but Jackson uses the actual length
        val cborOur = Cbor.encodeToByteArray(dataOurs)
        CBORMapper().writeValueAsBytes(dataTheirs)
        //assertThat(cborOur, equalTo(cborTheirs))

        val decodedFromCbor = Cbor.decodeFromByteArray<GreenCertificate>(cborOur)
        assertThat(decodedFromCbor, equalTo(dataOurs))
        assertThat(decodedFromCbor, equalTo(GreenCertificate.fromEuSchema(dataTheirs)))
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