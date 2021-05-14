import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.data.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(){
    println(Base45Encoder.encode(ByteArray(5){ i->i.toByte()}))
    val foo="Foo"
    val bar = js("externalTest(foo)")

    val cert = GreenCertificate("1.2", Person("Mustermann",null,"Max")
        , LocalDate(1980,6,8), listOf(Vaccination(ValueSetEntryAdapter("foo", ValueSetEntry("Voo","DE",true,"Soylent","ø")),ValueSetEntryAdapter("foo", ValueSetEntry("Voo","DE",true,"Soylent","ø")),ValueSetEntryAdapter("foo", ValueSetEntry("Voo","DE",true,"Soylent","ø")),ValueSetEntryAdapter("foo", ValueSetEntry("Voo","DE",true,"Soylent","ø")),9,100,
            LocalDate(2021,8,15),"AU","Royal Navy","HMCS"
        )),null,null)
    println(Json.encodeToString(cert))
    val input = DefaultCborService().encode(cert)
    val encode = Base45Encoder.encode(input)
    println(encode)
    val compressed = DefaultCompressorService().encode(input)
    println(Base45Encoder.encode(compressed))
    val decompressed = DefaultCompressorService().decode(compressed, VerificationResult())

    println(input)
    println(decompressed)
    println("Match:" + input.contentEquals(decompressed))

    val ceert = DefaultCborService().decode(input, VerificationResult())
    println(Json.encodeToString(ceert))
    println(bar)

    val cose = js("extrequire('cose-js')")
    println(cose)
}
