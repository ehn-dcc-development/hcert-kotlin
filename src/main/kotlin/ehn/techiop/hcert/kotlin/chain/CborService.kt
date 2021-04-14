package ehn.techiop.hcert.kotlin.chain

interface CborService {

    fun encode(input: VaccinationData): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): VaccinationData

}