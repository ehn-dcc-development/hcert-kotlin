package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

expect class PrefilledCertificateRepository : CertificateRepository {

    constructor(base64Encoded: String)

    constructor(vararg certificates: CertificateAdapter)

}


