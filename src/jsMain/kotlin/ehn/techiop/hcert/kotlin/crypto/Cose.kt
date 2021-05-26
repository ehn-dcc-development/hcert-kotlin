package ehn.techiop.hcert.kotlin.crypto

import Asn1js.fromBER
import Buffer
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.KeyType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import kotlinx.datetime.Instant
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Date
import kotlin.js.Json
import kotlin.js.Promise

internal object Cose {
    private val cose = js("require('cose-js')")

    @Suppress("UNUSED_VARIABLE")
    private fun internalVerify(
        data: dynamic,
        verifier: dynamic
    ): Uint8Array {
        val c = cose // needed for JS-magic
        return js("c.sign.verify(data, verifier)") as Uint8Array

    }

    fun verify(signedBitString: ByteArray, pubKey: PublicKey<*>):ByteArray =
        internalVerify(Buffer.from(signedBitString.toUint8Array()), pubKey.toCoseRepresentation()).toByteArray()

    @Suppress("UNUSED_VARIABLE")
    private fun internalSign(header: dynamic, data: dynamic, signer: dynamic): Promise<ByteArray> {
        val c = cose // needed for JS-magic
        return (js("c.sign.create(header, data, signer)") as Promise<ByteArray>)
    }

    fun sign(header: Json, input: ByteArray, privateKey: PrivateKey<*>) =
        internalSign(header, input, privateKey.toCoseRepresentation()).then { it }
}


class CoseEcKey(xC: dynamic, yC: dynamic) {
    constructor(x: ByteArray, y: ByteArray) : this(
        xC = Buffer.from(x.toUint8Array()),
        yC = Buffer.from(y.toUint8Array())
    )

    val key = Holder(xC, yC)

    class Holder(val x: dynamic, val y: dynamic)
}

// TODO is "d" sufficient?
class CoseEcPrivateKey(d: ByteArray) {
    val key = Holder(Buffer.from(d.toUint8Array()))

    class Holder(val d: dynamic)
}

class CoseJsEcPubKey(val xCoord: dynamic, val yCoord: dynamic, override val curve: CurveIdentifier) :
    EcPubKey<dynamic> {
    constructor(x: ByteArray, y: ByteArray, curve: CurveIdentifier) : this(
        xCoord = Buffer.from(x.toUint8Array()),
        yCoord = Buffer.from(y.toUint8Array()),
        curve = curve
    )

    override fun toCoseRepresentation() = CoseEcKey(xC = xCoord, yC = yCoord)
}

class CoseJsPrivateKey(val d: ByteArray, val curve: CurveIdentifier) : PrivateKey<dynamic> {
    override fun toCoseRepresentation() = CoseEcPrivateKey(d)
}

class JsCertificate(private val encoded: ByteArray) : Certificate<dynamic> {

    constructor(pem: String) : this(
        pem.lines().let { it.dropLast(1).drop(1) }.joinToString(separator = "").fromBase64()
    )

    private val cert = Uint8Array(encoded.toTypedArray()).let {
        val parsed = fromBER(it.buffer).result; pkijs.src.Certificate.Certificate(js("({'schema':parsed})"))
    }

    init {
        onlyCert = this
    }

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
        val keyInfo = (cert.subjectPublicKeyInfo as Json)["parsedKey"] as Json
        val x = keyInfo["x"]
        val y = keyInfo["y"]
        return CoseJsEcPubKey(
            xCoord = Uint8Array(buffer = x as ArrayBuffer),
            yCoord = Uint8Array(buffer = y as ArrayBuffer),
            curve = CurveIdentifier.P256
        )
    }

    override fun toTrustedCertificate(): TrustedCertificate {
        val pk = ((cert.subjectPublicKeyInfo as Json)["subjectPublicKey"] as Json)["valueBeforeDecode"] as ArrayBuffer

        return TrustedCertificate(
            Clock.System.now(),
            Clock.System.now(),
            byteArrayOf(),
            KeyType.EC,
            (cert.subjectPublicKeyInfo as Buffer).toByteArray(),
            listOf()
        )
    }

    companion object {
        var onlyCert: JsCertificate? = null
    }
}
