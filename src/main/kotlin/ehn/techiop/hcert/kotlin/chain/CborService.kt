package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.DigitalGreenCertificate

interface CborService {

    fun encode(input: DigitalGreenCertificate): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): DigitalGreenCertificate

}