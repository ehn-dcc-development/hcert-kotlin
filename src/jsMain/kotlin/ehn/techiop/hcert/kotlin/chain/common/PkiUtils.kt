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
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import ehn.techiop.hcert.kotlin.crypto.JsEcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsEcPubKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import hash
import kotlinx.datetime.Clock
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue
import pkijs.src.PublicKeyInfo.PublicKeyInfo
import pkijs.src.RelativeDistinguishedNames.RelativeDistinguishedNames
import pkijs.src.Time.Time
import tsstdlib.JsonWebKey
import kotlin.js.Date
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
    val serialNumber = Random.nextInt()
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
    //certificate.extensions = arrayOf<Extension>()
    // TODO Extensions, see JVM implementation

    val jwk = object : JsonWebKey {
        override var alg: String? = "EC"
        override var crv: String? = "P-256"
        override var kty: String? = "EC"
        override var x: String? = urlSafe((publicKey as JsEcPubKey).xCoord.toString("base64"))
        override var y: String? = urlSafe((publicKey as JsEcPubKey).yCoord.toString("base64"))
    }
    (certificate.subjectPublicKeyInfo as PublicKeyInfo).fromJSON(jwk)
    val algorithmIdentifier = AlgorithmIdentifier()
    algorithmIdentifier.algorithmId = "1.2.840.10045.4.3.2"
    certificate.signature = algorithmIdentifier
    certificate.signatureAlgorithm = algorithmIdentifier
    val sha256 = hash(Uint8Array(certificate.encodeTBS().toBER()))
    val priv = (privateKey as JsEcPrivKey).dValue
    val signatureValue = Uint8Array(privateKey.ec.sign(sha256, BN(priv)).toDER()).buffer

    certificate.signatureValue = BitString(
        object : LocalBitStringValueBlockParams {
            override var valueHex: ArrayBuffer? = signatureValue
        })
    certificate.tbs = certificate.encodeTBS().toBER()
    val encoded = Buffer((certificate.toSchema(true) as Sequence).toBER()).toByteArray()
    return JsCertificate(encoded.asBase64())
}


private fun urlSafe(input: String): String =
    input.replace("+", "-").replace("/", "_").replace("=", "")
