package ehn.techiop.hcert.kotlin.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ValueSetHolderTest {

    @Test
    fun testAccessByKey() {
        val found = ValueSetsInstanceHolder.INSTANCE.find("840539006")

        assertEquals(found.key, ("840539006"))
        assertEquals(found.valueSetEntry.display, ("COVID-19"))
        assertEquals(found.valueSetEntry.lang, ("en"))
        assertEquals(found.valueSetEntry.active, (true))
        assertEquals(
            found.valueSetEntry.version,
            ("http://snomed.info/sct/900000000000207008/version/20210131")
        )
        assertEquals(found.valueSetEntry.system, ("http://snomed.info/sct"))
    }

    @Test
    fun testAccessByCategoryKey() {
        val found = ValueSetsInstanceHolder.INSTANCE.find("vaccines-covid-19-names", "EU/1/20/1528")
        assertEquals(found.key, ("EU/1/20/1528"))
        assertEquals(found.valueSetEntry.display, ("Comirnaty"))
        assertEquals(found.valueSetEntry.lang, ("en"))
        assertEquals(found.valueSetEntry.active, (true))
        assertEquals(found.valueSetEntry.version, (""))
        assertEquals(
            found.valueSetEntry.system,
            ("https://ec.europa.eu/health/documents/community-register/html/")
        )
    }
}