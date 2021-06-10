package ehn.techiop.hcert.kotlin.chain.common

import Asn1js.*
import BN
import Buffer
import NodeRSA
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import hash
import kotlinx.datetime.Clock
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue
import pkijs.src.ExtKeyUsage.ExtKeyUsage
import pkijs.src.Extension.Extension
import pkijs.src.PublicKeyInfo.PublicKeyInfo
import pkijs.src.RelativeDistinguishedNames.RelativeDistinguishedNames
import pkijs.src.Time.Time
import tsstdlib.JsonWebKey
import kotlin.js.Date
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.Duration


@Suppress("UNUSED_VARIABLE")
actual fun selfSignCertificate(
    commonName: String,
    privateKey: PrivKey<*>,
    publicKey: PubKey<*>,
    contentType: List<ContentType>,
    clock: Clock
): Certificate<*> {
    val certificate = pkijs.src.Certificate.Certificate()
    certificate.version = 2
    val serialNumber = Random.nextInt().absoluteValue
    certificate.serialNumber = Integer(object : IntegerParams {
        override var value: Number? = serialNumber
    })
    val cn = PrintableString(object : LocalSimpleStringBlockParams {
        override var value: String? = commonName
    })
    (certificate.subject as RelativeDistinguishedNames).typesAndValues +=
        AttributeTypeAndValue(object {
            val type = "2.5.4.3"
            val value = cn
        })
    (certificate.issuer as RelativeDistinguishedNames).typesAndValues +=
        AttributeTypeAndValue(object {
            val type = "2.5.4.3"
            val value = cn
        })
    (certificate.notBefore as Time).value = Date(clock.now().toEpochMilliseconds())
    (certificate.notAfter as Time).value = Date(clock.now().plus(Duration.days(30)).toEpochMilliseconds())

    val extKeyUsage = ExtKeyUsage().also {
        it.keyPurposes = contentType.map { it.oid }.toTypedArray()
    }
    
    certificate.extensions = arrayOf(
        Extension(object {
            val extnID = "2.5.29.37"
            val critical = false
            val extnValue = (extKeyUsage.toSchema() as Sequence).toBER()
            val parsedValue = extKeyUsage
        })
    )

    val jwk = object : JsonWebKey {
        override var alg: String? = if (privateKey is EcPrivKey) "EC" else "RS256"
        override var crv: String? = if (privateKey is EcPrivKey) "P-256" else null
        override var kty: String? = if (privateKey is EcPrivKey) "EC" else "RSA"
        override var x: String? =
            if (privateKey is EcPrivKey) urlSafe((publicKey as JsEcPubKey).xCoord.toString("base64")) else null
        override var y: String? =
            if (privateKey is EcPrivKey) urlSafe((publicKey as JsEcPubKey).yCoord.toString("base64")) else null
        override var n: String? =
            if (privateKey is EcPrivKey) null else urlSafe(
                stripLeadingZero((publicKey as JsRsaPubKey).toCoseRepresentation().n).toString("base64")
            )
        override var e: String? =
            if (privateKey is EcPrivKey) null else urlSafe(
                Buffer(Int32Array(arrayOf((publicKey as JsRsaPubKey).toCoseRepresentation().e.toInt())).buffer)
                    .toString("base64")
            )
    }
    (certificate.subjectPublicKeyInfo as PublicKeyInfo).fromJSON(jwk)
    val algorithmIdentifier = AlgorithmIdentifier()
    algorithmIdentifier.algorithmId = if (privateKey is EcPrivKey) "1.2.840.10045.4.3.2" else "1.2.840.113549.1.1.11"
    certificate.signature = algorithmIdentifier
    certificate.signatureAlgorithm = algorithmIdentifier
    val data = Uint8Array(certificate.encodeTBS().toBER())
    val signatureValue = if (privateKey is EcPrivKey) {
        val sha256 = hash(data)
        val priv = (privateKey as JsEcPrivKey).dValue
        Uint8Array(privateKey.ec.sign(sha256, BN(priv)).toDER()).buffer
    } else {
        privateKey as JsRsaPrivKey
        Uint8Array(NodeRSA().importKey(privateKey.raw as NodeRSA.KeyComponentsPrivate).sign(Buffer(data))).buffer
    }
    certificate.signatureValue = BitString(
        object : LocalBitStringValueBlockParams {
            override var valueHex: ArrayBuffer? = signatureValue
        })
    certificate.tbs = certificate.encodeTBS().toBER()
    val encoded = Buffer((certificate.toSchema(true) as Sequence).toBER()).toByteArray()
    return JsCertificate(encoded.asBase64())
}

// We'll need to strip the leading zero from the Buffer
// because ASN.1 will add it's own leading zero, if needed
private fun stripLeadingZero(n: Buffer): Buffer {
    return if (n.readUInt8(0) == 0) n.slice(1) else n
}

internal fun urlSafe(input: String): String =
    input.replace("+", "-").replace("/", "_").replace("=", "")
