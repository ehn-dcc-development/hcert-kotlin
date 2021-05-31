package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService

expect class FileBasedCryptoService constructor(pemEncodedKeyPair: String, pemEncodedCertificate: String) :
    CryptoService