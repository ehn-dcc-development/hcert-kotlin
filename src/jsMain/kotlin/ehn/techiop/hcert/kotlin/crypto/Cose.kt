package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Json
import kotlin.js.Promise

internal object Buffer {
    private val buffer = js("extrequire('buffer').Buffer")
    internal fun from(arr: ByteArray): dynamic {
        //yes, we ned those assignments! DO NOT REMOVE!
        val b = buffer
        val d = Uint8Array(arr.toTypedArray())
        return js("b.from(d)")
    }
}

internal object Cbor{
    private val cbor =js("extrequire('cbor')")
    fun decode(data:ByteArray):dynamic{
        val c= cbor
        val d = Buffer.from(data)
        return js("c.decodeFirstSync(d)")
    }
}

internal object Cose {
    private val cose = js("extrequire('cose-js')")


    private fun internalVerify(
        data: dynamic,
        verifier: dynamic
    ): Promise<Any> {
        //YES WE NEED THIS ASSIGNMENT
        val c = cose
        return (js("c.sign.verify(data, verifier)") as Promise<Any>)

    }

    fun verify(
        signedBitString: ByteArray,
        pubKey: CoseJsEcPubKey
    ) =
        internalVerify(Buffer.from(signedBitString), pubKey.toCoseRepresentation())

}


class CoseEcKey(x: ByteArray, y: ByteArray) {
    val key = Holder(Buffer.from(x), Buffer.from(y))

    class Holder(val x: dynamic, val y: dynamic)
}

// TODO is "d" sufficient?
class CoseEcPrivateKey(d: ByteArray) {
    val key = Holder(Buffer.from(d))
    class Holder(val d: dynamic)
}

class CoseJsEcPubKey(val xCoord: ByteArray, val yCoord: ByteArray, override val curve: CurveIdentifier) :
    EcPubKey<dynamic> {
    override fun toCoseRepresentation() = CoseEcKey(xCoord, yCoord)
}

class CoseJsPrivateKey(val d: ByteArray, val curve: CurveIdentifier): PrivateKey<dynamic> {
    override fun toCoseRepresentation() = CoseEcPrivateKey(d)
}

class JsCertificate(): Certificate<dynamic> {
    override fun getValidContentTypes(): List<ContentType> {
        TODO("Not yet implemented")
    }

    override fun getValidFrom(): Instant {
        TODO("Not yet implemented")
    }

    override fun getValidUntil(): Instant {
        TODO("Not yet implemented")
    }

    override fun getPublicKey(): PublicKey<*> {
        TODO("Not yet implemented")
    }
}
