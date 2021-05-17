package ehn.techiop.hcert.kotlin.crypto

interface PublicKey<T> {
    fun toCoseRepresenation(): T
}

interface EcPubKey<T> : PublicKey<T> {
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
    P512("P-512", 3U),
    X25519("X25519", 4U),
    X448("X448", 5U),
    ED25519("Ed25519", 6U),
    Ed448("Ed448", 7U),
    SECP256K1("secp256k1", 0U)
}