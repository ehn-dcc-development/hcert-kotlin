package ehn.techiop.hcert.kotlin.chain.common

import Asn1js.BitString
import Asn1js.Integer
import Asn1js.PrintableString
import Asn1js.Sequence
import Buffer
import cose.CosePublicKey
import cose.EcCosePublicKey
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import hash
import kotlinx.datetime.Clock
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue
import pkijs.src.ECPrivateKey.ECPrivateKey
import pkijs.src.Extension.Extension
import pkijs.src.PublicKeyInfo.PublicKeyInfo
import pkijs.src.RelativeDistinguishedNames.RelativeDistinguishedNames
import pkijs.src.Time.Time
import tsstdlib.CryptoKey
import tsstdlib.JsonWebKey
import kotlin.js.Date
import kotlin.random.Random
import kotlin.time.Duration


actual fun selfSignCertificate(
    commonName: String,
    privateKey: PrivKey<*>,
    publicKey: PubKey<*>,
    contentType: List<ContentType>,
    clock: Clock
): Certificate<*> {
    val certificate = pkijs.src.Certificate.Certificate()

    certificate.version=2


    @Suppress("UNUSED_VARIABLE") val serialNumber = Random.nextInt()
    certificate.serialNumber = Integer(js("({'value': serialNumber})"))
    @Suppress("UNUSED_VARIABLE") val cn = PrintableString(js("({'value': commonName})"))
    (certificate.subject as RelativeDistinguishedNames).typesAndValues +=
        AttributeTypeAndValue(js("({ 'type': '2.5.4.3', 'value': cn})"))
    (certificate.issuer as RelativeDistinguishedNames).typesAndValues +=
        AttributeTypeAndValue(js("({ 'type': '2.5.4.3', 'value': cn})"))
    (certificate.notBefore as Time).value = Date(clock.now().toEpochMilliseconds())
    (certificate.notAfter as Time).value = Date(clock.now().plus(Duration.days(30)).toEpochMilliseconds())
    certificate.extensions = arrayOf<Extension>()
    // TODO Extensions, see JVM implementation
    // TODO Import Key Sync
    val jwk = object: JsonWebKey{
        override var alg: String? = "EC"
        override var crv: String? = "P-256"
        override var x: String? = (publicKey.toCoseRepresentation() as EcCosePublicKey).x.toByteArray().asBase64()
        override var y: String? = (publicKey.toCoseRepresentation() as EcCosePublicKey).y.toByteArray().asBase64()
    }
    (certificate.subjectPublicKeyInfo as PublicKeyInfo).fromJSON(jwk)
    // TODO Sign sync
    val
            algorithmIdentifier = AlgorithmIdentifier()
    algorithmIdentifier.algorithmId="1.2.840.10045.4.3.2"
    certificate.signature = algorithmIdentifier
    certificate.signatureAlgorithm = algorithmIdentifier
    certificate.tbs=certificate.encodeTBS().toBER(js("false"))
    val sha256= hash(Uint8Array( certificate.tbs))
    val signatureValue= (privateKey as JsEcPrivKey).keyPair.sign(sha256).toDER()


    certificate.signatureValue = BitString(js("({valueHex: signatureValue})"))
    return JsCertificate(Buffer((certificate.toSchema(true) as Sequence).toBER()).toByteArray())
}

