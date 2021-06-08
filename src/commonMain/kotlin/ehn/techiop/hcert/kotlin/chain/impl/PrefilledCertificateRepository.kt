package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.crypto.Certificate

expect class PrefilledCertificateRepository : CertificateRepository {

    constructor(input: ByteArray)

    constructor(base64Encoded: String)

    constructor(vararg certificates: Certificate<*>)

}


