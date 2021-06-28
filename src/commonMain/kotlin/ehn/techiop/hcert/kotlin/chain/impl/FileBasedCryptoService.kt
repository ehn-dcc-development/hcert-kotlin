package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.crypto.CryptoAdapter

class FileBasedCryptoService(
    pemEncodedPrivateKey: String,
    pemEncodedCertificate: String
) : DefaultCryptoService(CryptoAdapter(pemEncodedPrivateKey, pemEncodedCertificate))