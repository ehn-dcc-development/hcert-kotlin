package ehn.techiop.hcert.kotlin.trust

/**
 * Document signing certificates may include an extended key usage extension,
 * that contains a list of OID values, that map to valid HCERT contents,
 * that this certificate is allowed to sign.
 */
enum class ContentType(val oid: String, val alternativeOid: Collection<String>) {

    TEST("1.3.6.1.4.1.1847.2021.1.1", listOf("1.3.6.1.4.1.0.1847.2021.1.1")),

    VACCINATION("1.3.6.1.4.1.1847.2021.1.2", listOf("1.3.6.1.4.1.0.1847.2021.1.2")),

    RECOVERY("1.3.6.1.4.1.1847.2021.1.3", listOf("1.3.6.1.4.1.0.1847.2021.1.3"));

    companion object {
        fun findByOid(oid: String): ContentType? {
            return values().firstOrNull { it.oid == oid || it.alternativeOid.contains(oid) }
        }
    }

}
