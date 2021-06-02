package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository

expect class PrefilledCertificateRepository : CertificateRepository {

    constructor(input: ByteArray)

    constructor(base64Encoded: String)

}


