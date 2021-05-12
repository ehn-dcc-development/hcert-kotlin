import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder

fun main(){
    println(Base45Encoder.encode(ByteArray(5){ i->i.toByte()}))
    val foo="Foo"
    val bar = js("externalTest(foo)")
    println(bar)
}