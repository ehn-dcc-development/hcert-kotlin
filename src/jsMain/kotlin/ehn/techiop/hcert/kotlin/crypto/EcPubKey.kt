package ehn.techiop.hcert.kotlin.crypto

class CoseEcKey(x: ByteArray, y: ByteArray) {
    //COSE-JS requires this
    val key = Holder(x, y)

    class Holder(xC: ByteArray, yC: ByteArray) {
        //do not join this with the assignment.
        //do not make this constructor properties
        //wither will break things, because variables within the JS call will not resolve
        val x: dynamic
        val y: dynamic

        init {
            x = js("var Buffer= extrequire('buffer');Buffer.Buffer.from(new Uint8Array(xC))")
            y = js("var Buffer= extrequire('buffer');Buffer.Buffer.from(new Uint8Array(yC))")
        }
    }
}

class CoseJsEcPubKey(val xCoord: ByteArray, val yCoord: ByteArray, override val curve: CurveIdentifier) :
    EcPubKey<dynamic> {
    override fun toCoseRepresenation() = CoseEcKey(xCoord, yCoord)
}