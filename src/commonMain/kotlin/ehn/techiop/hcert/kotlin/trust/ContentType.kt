package ehn.techiop.hcert.kotlin.trust


enum class ContentType(val oid: String) {

    TEST("1.3.6.1.4.1.0.1847.2021.1.1"),

    VACCINATION("1.3.6.1.4.1.0.1847.2021.1.2"),

    RECOVERY("1.3.6.1.4.1.0.1847.2021.1.3");

    companion object {
        fun findByOid(oid: String): ContentType? {
            return ContentType.values().firstOrNull { it.oid == oid }
        }
    }

}
