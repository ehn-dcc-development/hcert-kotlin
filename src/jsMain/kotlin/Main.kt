import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.Person
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(){
    println(Base45Encoder.encode(ByteArray(5){ i->i.toByte()}))
    val foo="Foo"
    val bar = js("externalTest(foo)")
    val cert = GreenCertificate("1.2", Person("Mustermann",null,"Max")
    , LocalDate(1980,6,8),null,null,null)
    println(Json.encodeToString(cert))
    println(Base45Encoder.encode(DefaultCborService().encode(cert)))
    println(bar)
}