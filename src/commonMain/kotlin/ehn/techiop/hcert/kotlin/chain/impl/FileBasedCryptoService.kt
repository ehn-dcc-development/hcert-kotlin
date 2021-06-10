package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService

expect class FileBasedCryptoService constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) :
    CryptoService