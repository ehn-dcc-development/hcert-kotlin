package ehn.techiop.hcert.kotlin.chain

enum class VerificationDecision {
    GOOD,
    FAIL_QRCODE,
    FAIL_VALIDITY,
    FAIL_SIGNATURE
}