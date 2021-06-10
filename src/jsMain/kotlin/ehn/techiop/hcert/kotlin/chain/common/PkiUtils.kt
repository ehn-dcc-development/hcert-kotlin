package ehn.techiop.hcert.kotlin.chain.common

import Asn1js.BitString
import Asn1js.Integer
import Asn1js.IntegerParams
import Asn1js.LocalBitStringValueBlockParams
import Asn1js.LocalSimpleStringBlockParams
import Asn1js.PrintableString
import Asn1js.Sequence
import BN
import Buffer
import NodeRSA
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.EcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsEcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsEcPubKey
import ehn.techiop.hcert.kotlin.crypto.JsRsaPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsRsaPubKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
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


actual class PkiUtils {
    @Suppress("UNUSED_VARIABLE")
    actual fun selfSignCertificate(
        commonName: String,
        privateKey: PrivKey<*>,
        publicKey: PubKey<*>,
        keySize: Int,
        contentType: List<ContentType>,
        clock: Clock
    ): CertificateAdapter {
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

        val jwk = buildJsonWebKey(privateKey, publicKey, keySize)
        (certificate.subjectPublicKeyInfo as PublicKeyInfo).fromJSON(jwk)
        val algorithmIdentifier = AlgorithmIdentifier()
        algorithmIdentifier.algorithmId =
            if (privateKey is EcPrivKey) "1.2.840.10045.4.3.2" else "1.2.840.113549.1.1.11"
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
        return CertificateAdapter(encoded.asBase64())
    }

    private fun buildJsonWebKey(privateKey: PrivKey<*>, publicKey: PubKey<*>, keySize: Int) = when {
        privateKey is JsEcPrivKey && publicKey is JsEcPubKey -> {
            object : JsonWebKey {
                override var alg: String? = "EC"
                override var crv: String? = if (keySize == 384) "P-384" else "P-256"
                override var kty: String? = "EC"
                override var x: String? = publicKey.xCoord.toBase64UrlString()
                override var y: String? = publicKey.yCoord.toBase64UrlString()
            }
        }
        privateKey is JsRsaPrivKey && publicKey is JsRsaPubKey -> {
            object : JsonWebKey {
                override var alg: String? = "RS256"
                override var kty: String? = "RSA"
                override var n: String? = stripLeadingZero(publicKey.toCoseRepresentation().n).toBase64UrlString()
                override var e: String? =
                    Buffer(Int32Array(arrayOf(publicKey.toCoseRepresentation().e.toInt())).buffer).toBase64UrlString()
            }
        }
        else -> throw IllegalArgumentException("KeyType")
    }

    // We'll need to strip the leading zero from the Buffer
    // because ASN.1 will add it's own leading zero, if needed
    private fun stripLeadingZero(n: Buffer): Buffer {
        return if (n.readUInt8(0) == 0) n.slice(1) else n
    }

}