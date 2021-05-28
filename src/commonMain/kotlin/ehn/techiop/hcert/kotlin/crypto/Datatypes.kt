package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant


interface Certificate<T> {
    val validContentTypes: List<ContentType>
    val validFrom: Instant
    val validUntil: Instant
    val publicKey: PubKey<*>
    fun toTrustedCertificate(): TrustedCertificateV2
    val kid: ByteArray
}

interface PrivKey<T> {
    fun toCoseRepresentation(): T
}

interface EcPrivKey<T> : PrivKey<T>
interface RsaPrivKey<T> : PrivKey<T>

interface PubKey<T> {
    fun toCoseRepresentation(): T
}

interface EcPubKey<T> : PubKey<T>

enum class CoseHeaderKeys(val value: Int) {
    Algorithm(1),

    //CONTENT_TYPE(3),
    KID(4),
    IV(5),
    //CriticalHeaders(2),
    //CounterSignature(7),
    //PARTIAL_IV(6),
    //CounterSignature0(9),
    //ECDH_EPK(-1),
    //ECDH_SPK(-2),
    //ECDH_SKID(-3),
    //HKDF_Salt(-20),
    //HKDF_Context_PartyU_ID(-21),
    //HKDF_Context_PartyU_nonce(-22),
    //HKDF_Context_PartyU_Other(-23),
    //HKDF_Context_PartyV_ID(-24),
    //HKDF_Context_PartyV_nonce(-25),
    //HKDF_Context_PartyV_Other(-26),
    //HKDF_SuppPub_Other(-999),
    //HKDF_SuppPriv_Other(-998);
}

enum class CwtHeaderKeys(val value: Int) {
    ISSUER(1),
    SUBJECT(2),
    AUDIENCE(3),
    EXPIRATION(4),
    NOT_BEFORE(5),
    ISSUED_AT(6),
    CWT_ID(7),
    HCERT(-260),
}
