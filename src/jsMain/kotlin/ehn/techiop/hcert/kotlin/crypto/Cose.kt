package ehn.techiop.hcert.kotlin.crypto

import Asn1js.fromBER
import Buffer
import Hash
import cose.CosePublicKey
import cose.EcCosePrivateKey
import cose.RsaCosePublicKey
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.trust.*
import kotlinx.datetime.Instant
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import pkijs.src.ExtKeyUsage.ExtKeyUsage
import pkijs.src.Time.Time
import kotlin.js.Json
import kotlin.js.Promise

internal object Cose {
    fun verifySync(signedBitString: ByteArray, pubKey: PublicKey<*>): ByteArray {
        val key = pubKey.toCoseRepresentation() as CosePublicKey
        val verifier = object : cose.Verifier {
            override val key = key
        }

        return cose.sign.verifySync(Buffer.from(signedBitString.toUint8Array()), verifier).toByteArray()
    }

    fun sign(header: Json, input: ByteArray, privateKey: PrivateKey<*>): Promise<Buffer> {
        val key = privateKey.toCoseRepresentation() as EcCosePrivateKey
        val signer = object : cose.Signer {
            override val key = key
        }
        return cose.sign.create(header.asDynamic(), Buffer(input.toUint8Array()), signer).then { it }
    }
}

class CoseJsEcPubKey(val xCoord: dynamic, val yCoord: dynamic, override val curve: CurveIdentifier) :
    EcPubKey<dynamic> {
    constructor(x: ByteArray, y: ByteArray, curve: CurveIdentifier) : this(
        xCoord = Buffer.from(x.toUint8Array()),
        yCoord = Buffer.from(y.toUint8Array()),
        curve = curve
    )

    override fun toCoseRepresentation(): dynamic {
        val key = object : cose.EcCosePublicKey {
            override val x = xCoord
            override val y = yCoord
        }
        return key
    }
}

class CoseJsRsaPubKey(val modulus: ArrayBuffer, val publicExponent: ArrayBuffer) :
    PublicKey<dynamic> {

    override fun toCoseRepresentation(): RsaCosePublicKey {
        return object : RsaCosePublicKey {
            override val n = Buffer.from(Uint8Array(modulus))
            override val e = Buffer.from(Uint8Array(publicExponent))
        }
    }
}

class CoseJsPrivateKey(val da: ByteArray, val curve: CurveIdentifier) : PrivateKey<dynamic> {
    override fun toCoseRepresentation(): EcCosePrivateKey {
        val key = object : cose.EcCosePrivateKey {
            override val d = Buffer(da.toUint8Array())
        }
        return key
    }
}

class JsCertificate(val encoded: ByteArray) : Certificate<dynamic> {

    constructor(pem: String) : this(
        pem.lines().joinToString(separator = "").fromBase64()
    )

    private val cert = Uint8Array(encoded.toTypedArray()).let { bytes ->
        fromBER(bytes.buffer).result.let { pkijs.src.Certificate.Certificate(js("({'schema':it})")) }
    }


    override fun getValidContentTypes(): List<ContentType> {
        val extKeyUsage = cert.extensions.firstOrNull {
            it.extnID == "2.5.29.37"
        }?.parsedValue as ExtKeyUsage?
        val contentTypes = mutableSetOf<ContentType>()
        extKeyUsage?.let {
            it.keyPurposes.forEach { oidStr ->
                when (oidStr) {
                    oidRecovery -> contentTypes.add(ContentType.RECOVERY)
                    oidTest -> contentTypes.add(ContentType.TEST)
                    oidVaccination -> contentTypes.add(ContentType.VACCINATION)
                }
            }
        }
        if (contentTypes.isEmpty()) {
            contentTypes.add(ContentType.RECOVERY)
            contentTypes.add(ContentType.TEST)
            contentTypes.add(ContentType.VACCINATION)
        }
        return contentTypes.toList()
    }

    override fun getValidFrom(): Instant {
        val date = (cert.notBefore as Time).value
        return Instant.parse(date.toISOString())
    }

    override fun getValidUntil(): Instant {
        val date = (cert.notAfter as Time).value
        return Instant.parse(date.toISOString())
    }

    override fun getPublicKey(): PublicKey<*> {
        val publicKeyOID = ((cert.subjectPublicKeyInfo as Json)["algorithm"] as Json)["algorithmId"] as String
        //TODO investigate asn1 lib to use proper OID objects
        val isEC = publicKeyOID == "1.2.840.10045.2.1"
        val isRSA = publicKeyOID.startsWith("1.2.840.113549")
        val keyInfo = (cert.subjectPublicKeyInfo as Json)["parsedKey"] as Json
        if (isEC) {
            val x = keyInfo["x"]
            val y = keyInfo["y"]
            return CoseJsEcPubKey(
                xCoord = Uint8Array(buffer = x as ArrayBuffer),
                yCoord = Uint8Array(buffer = y as ArrayBuffer),
                curve = CurveIdentifier.P256
            )
        } else if (isRSA) {
            val kValue = (keyInfo["modulus"] as Json)["valueBlock"] as Json
            console.info(JSON.stringify(kValue))
            val mod = kValue["valueHex"] as ArrayBuffer
            console.info(JSON.stringify(mod))
            val exponent = ((keyInfo["publicExponent"] as Json)["valueBlock"] as Json)["valueHex"] as ArrayBuffer
            console.info(JSON.stringify(exponent))
            return CoseJsRsaPubKey(mod, exponent)
        } else TODO("Not implemeneted")
    }

    override fun toTrustedCertificate(): TrustedCertificateV2 {
        return TrustedCertificateV2(calcKid(), encoded)
    }

    override fun calcKid(): ByteArray {
        val hash = Hash()
        hash.update(encoded.toUint8Array())
        return hash.digest().toByteArray().copyOf(8)
    }
}
