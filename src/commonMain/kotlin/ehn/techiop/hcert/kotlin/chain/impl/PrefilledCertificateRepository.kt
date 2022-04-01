package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<CertificateAdapter>()

    constructor(vararg certificates: CertificateAdapter) {
        certificates.forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        pemEncodedCertificates.forEach { list += CertificateAdapter(it) }
    }

    constructor(pemEncoded: String) {
        list += CertificateAdapter(pemEncoded)
    }

    constructor()

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<CertificateAdapter> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty())
            throw VerificationException(
                Error.KEY_NOT_IN_TRUST_LIST,
                "kid not found",
                details = mapOf("hexEncodedKid" to kid.toHexString())
            )

        return certList
    }

    override fun toString() = "PrefilledCertificateRepository(" + list.joinToString { it.prettyPrint() } + ")"


}


