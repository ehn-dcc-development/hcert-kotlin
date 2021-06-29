package ehn.techiop.hcert.kotlin.crypto

import Asn1js.PrintableString
import Asn1js.fromBER
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.Hash
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import pkijs.src.ExtKeyUsage.ExtKeyUsage
import pkijs.src.RSAPublicKey.RSAPublicKey
import pkijs.src.RelativeDistinguishedNames.RelativeDistinguishedNames
import pkijs.src.Time.Time
import kotlin.js.Json

/**
 * Primary constructor is nicely exposed to javascript by default;
 * secondary constructors not without any custom annotations;
 * so we make the pem-parsing constructor the default one
 */
actual class CertificateAdapter actual constructor(_encoded: ByteArray) {

    actual val encoded = _encoded

    @JsName("fromPem")
    actual constructor(pemEncoded: String) : this(
        pemEncoded
            .replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")
            .lines().joinToString(separator = "")
            .fromBase64()
    )

    internal val cert = Uint8Array(_encoded.toTypedArray()).let { bytes ->
        fromBER(bytes.buffer).result.let {
            pkijs.src.Certificate.Certificate(
                object {
                    @Suppress("unused")
                    val schema = it
                })
        }
    }

    actual val validContentTypes: List<ContentType>
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

    actual val validFrom: Instant
        get() {
            val date = (cert.notBefore as Time).value
            return Instant.parse(date.toISOString())
        }

    actual val validUntil: Instant
        get() {
            val date = (cert.notAfter as Time).value
            return Instant.parse(date.toISOString())
        }

    actual val subjectCountry: String?
        get() {
            for (tav in (cert.subject as RelativeDistinguishedNames).typesAndValues) {
                if (tav.type.toString() == "2.5.4.6" && tav.value is PrintableString)
                    return (tav.value as PrintableString).valueBlock.value
            }
            val input = cert.subject.toString()
            return Regex("C=[^,]*").find(input)?.value
        }

    actual val publicKey: PubKey
        get() {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val publicKeyOID = ((cert.subjectPublicKeyInfo as Json)["algorithm"] as Json)["algorithmId"] as String
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
                else -> throw IllegalArgumentException("KeyType")
            }
        }

    actual fun toTrustedCertificate(): TrustedCertificateV2 {
        return TrustedCertificateV2(kid, encoded)
    }

    actual val kid: ByteArray
        get() = Hash(encoded).calc().copyOf(8)

}
