package ehn.techiop.hcert.kotlin.chain.common

import Asn1js.Integer
import Asn1js.PrintableString
import Asn1js.Sequence
import Buffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue
import pkijs.src.Extension.Extension
import pkijs.src.PublicKeyInfo.PublicKeyInfo
import pkijs.src.RelativeDistinguishedNames.RelativeDistinguishedNames
import pkijs.src.Time.Time
import tsstdlib.CryptoKey
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
    (certificate.subjectPublicKeyInfo as PublicKeyInfo).importKey(publicKey.toCoseRepresentation() as CryptoKey)
    // TODO Sign sync
    certificate.sign(privateKey.toCoseRepresentation() as CryptoKey, hashAlgorithm = "SHA256")
    return JsCertificate(Buffer((certificate.toSchema(true) as Sequence).toBER()).toByteArray())
}

