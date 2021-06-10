package ehn.techiop.hcert.kotlin.crypto

import Asn1js.fromBER
import Buffer
import cose.*
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.Hash
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import elliptic.EC
import elliptic.EcKeyPair
import elliptic.EcPublicKey
import kotlinx.datetime.Instant
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import pkijs.src.ExtKeyUsage.ExtKeyUsage
import pkijs.src.RSAPublicKey.RSAPublicKey
import pkijs.src.Time.Time
import kotlin.js.Json

internal object Cose {
    fun verifySync(signedBitString: ByteArray, pubKey: PubKey<*>): ByteArray {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val key = pubKey.toCoseRepresentation() as CosePublicKey
        val verifier = object : Verifier {
            override val key = key
        }
        return sign.verifySync(Buffer.from(signedBitString.toUint8Array()), verifier).toByteArray()
    }

    fun sign(header: dynamic, input: ByteArray, privKey: PrivKey<*>): Buffer {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val key = privKey.toCoseRepresentation() as CosePrivateKey
        val signer = object : Signer {
            override val key = key
        }
        return sign.createSync(header, Buffer(input.toUint8Array()), signer)
    }
}

class JsEcPubKey(val xCoord: Buffer, val yCoord: Buffer) : EcPubKey<dynamic> {

    constructor(ecPublicKey: EcPublicKey) : this(
        ecPublicKey.getX().toArrayLike(Buffer),
        ecPublicKey.getY().toArrayLike(Buffer),
    )

    constructor(ecKeyPair: EcKeyPair) : this(ecKeyPair.getPublic())

    constructor(x: Uint8Array, y: Uint8Array) : this(
        xCoord = Buffer.from(x),
        yCoord = Buffer.from(y),
    )

    override fun toCoseRepresentation(): EcCosePublicKey {
        return object : EcCosePublicKey {
            override val x = xCoord
            override val y = yCoord
        }
    }

}

class JsRsaPubKey(val modulus: ArrayBuffer, val publicExponent: Number) :
    PubKey<dynamic> {

    override fun toCoseRepresentation(): RsaCosePublicKey = object : RsaCosePublicKey {
        override val n = Buffer.from(Uint8Array(modulus))
        override val e = publicExponent
    }
}

class JsEcPrivKey(val dValue: Buffer, val ec: EC) : EcPrivKey<EcCosePrivateKey> {

    constructor(keyPair: EcKeyPair) : this(keyPair.getPrivate().toArrayLike(Buffer), keyPair.ec)

    override fun toCoseRepresentation(): EcCosePrivateKey = object : EcCosePrivateKey {
        override val d: Buffer = dValue
    }
}


class JsRsaPrivKey(val raw: Json) : RsaPrivKey<dynamic> {

    override fun toCoseRepresentation(): RsaCosePrivateKey = object : RsaCosePrivateKey {
        override val p: Buffer = raw["p"] as Buffer
        override val q: Buffer = raw["q"] as Buffer
        override val dp: Buffer = raw["dmp1"] as Buffer
        override val dq: Buffer = raw["dmq1"] as Buffer
        override val qi: Buffer = raw["coeff"] as Buffer
        override val d: Buffer = raw["d"] as Buffer
        override val n: Buffer = raw["n"] as Buffer
        override val e: Number = raw["e"] as Number

    }
}

/**
 * Primary constructor is nicely exposed to javascript by default;
 * secondary constructors not without any custom annotations;
 * so we make the pem-parsing constructor the default one
 */
class JsCertificate(val pemEncodedCertificate: String) : CertificateAdapter<dynamic> {

    override val encoded: ByteArray = pemEncodedCertificate
        .replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replace("\n", "")
        .fromBase64()

    @JsName("fromPem")
    constructor(encoded: ByteArray) : this(encoded.asBase64())

    internal val cert = Uint8Array(encoded.toTypedArray()).let { bytes ->
        fromBER(bytes.buffer).result.let {
            pkijs.src.Certificate.Certificate(
                object {
                    @Suppress("unused")
                    val schema = it
                })
        }
    }


    override val validContentTypes: List<ContentType>
        get() {
            if (cert.extensions == undefined)
                return ContentType.values().toList()
            val extKeyUsage = cert.extensions.firstOrNull {
                it.extnID == "2.5.29.37"
            }?.parsedValue as ExtKeyUsage?
            val contentTypes = mutableSetOf<ContentType>()
            extKeyUsage?.let {
                it.keyPurposes.forEach { oidStr ->
                    ContentType.findByOid(oidStr)?.let { contentTypes.add(it) }
                }
            }
            return contentTypes.ifEmpty { ContentType.values().toList() }.toList()
        }

    override val validFrom: Instant
        get() {
            val date = (cert.notBefore as Time).value
            return Instant.parse(date.toISOString())
        }

    override val validUntil: Instant
        get() {
            val date = (cert.notAfter as Time).value
            return Instant.parse(date.toISOString())
        }

    override val publicKey: PubKey<*>
        get() {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val publicKeyOID = ((cert.subjectPublicKeyInfo as Json)["algorithm"] as Json)["algorithmId"] as String
            //TODO investigate asn1 lib to use proper OID objects
            val isEC = publicKeyOID == "1.2.840.10045.2.1"
            val isRSA = publicKeyOID.startsWith("1.2.840.113549")

            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val keyInfo = (cert.subjectPublicKeyInfo as Json)["parsedKey"] as Json
            return when {
                isEC -> {
                    val x = keyInfo["x"] as ArrayBuffer
                    val y = keyInfo["y"] as ArrayBuffer
                    JsEcPubKey(Uint8Array(x), Uint8Array(y))
                }
                isRSA -> {
                    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                    val kValue = (keyInfo["modulus"] as Json)["valueBlock"] as Json
                    val rsaKey = keyInfo as RSAPublicKey
                    val mod = kValue["valueHex"] as ArrayBuffer
                    val exponent = rsaKey.publicExponent.valueBlock.valueDec
                    JsRsaPubKey(mod, exponent)
                }
                else -> TODO("Not implemented")
            }
        }

    override fun toTrustedCertificate(): TrustedCertificateV2 {
        return TrustedCertificateV2(kid, encoded)
    }

    override val kid: ByteArray
        get() = Hash(encoded).calc().copyOf(8)
}
