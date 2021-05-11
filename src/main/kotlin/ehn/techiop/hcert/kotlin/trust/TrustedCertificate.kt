package ehn.techiop.hcert.kotlin.trust

import COSE.OneKey
import java.time.Instant


interface TrustedCertificate {

    fun getKid(): ByteArray

    fun buildOneKey(): OneKey

    fun getValidContentTypes(): List<ContentType>

    fun getValidFrom(): Instant

    fun getValidUntil(): Instant

}