package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService

expect class VerificationCoseService constructor(repository: CertificateRepository) : CoseService {

}