package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant


interface Certificate<T> {
    fun getValidContentTypes(): List<ContentType>
    fun getValidFrom(): Instant
    fun getValidUntil(): Instant
    fun getPublicKey(): PubKey<*>
    fun toTrustedCertificate(): TrustedCertificateV2
    fun calcKid(): ByteArray
}

interface PrivKey<T> {
    fun toCoseRepresentation(): T
}

interface EcPrivKey<T>:PrivKey<T>
interface RsaPrivKey<T>:PrivKey<T>

interface PubKey<T> {
   fun toCoseRepresentation(): T
}

interface EcPubKey<T> : PubKey<T>
/*
{
    val curve: CurveIdentifier
}

//    +---------+----------+-------+------------------------------------+
//    | name    | key type | value | description                        |
//    +---------+----------+-------+------------------------------------+
//    | P-256   | EC2      | 1     | NIST P-256 also known as secp256r1 |
//    |         |          |       |                                    |
//    | P-384   | EC2      | 2     | NIST P-384 also known as secp384r1 |
//    |         |          |       |                                    |
//    | P-521   | EC2      | 3     | NIST P-521 also known as secp521r1 |
//    |         |          |       |                                    |
//    | X25519  | OKP      | 4     | X25519 for use w/ ECDH only        |
//    |         |          |       |                                    |
//    | X448    | OKP      | 5     | X448 for use w/ ECDH only          |
//    |         |          |       |                                    |
//    | Ed25519 | OKP      | 6     | Ed25519 for use w/ EdDSA only      |
//    |         |          |       |                                    |
//    | Ed448   | OKP      | 7     | Ed448 for use w/ EdDSA only        |
//    +---------+----------+-------+------------------------------------+
enum class CurveIdentifier(val jsonWebCurveID: String, val coseCurveID: UByte) {
    P256("P-256", 1U),
    P384("P-384", 2U),

    //P512("P-512", 3U),
    //X25519("X25519", 4U),
    //X448("X448", 5U),
    ED25519("Ed25519", 6U),
    //Ed448("Ed448", 7U),
    //SECP256K1("secp256k1", 0U)
}
*/

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
