package ehn.techiop.hcert.kotlin.data

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class ValueSetHolderTest {

    @Test
    fun testAccessByKey() {
        val found = ValueSetHolder.INSTANCE.find("840539006")

        assertThat(found.key, equalTo("840539006"))
        assertThat(found.valueSetEntry.display, equalTo("COVID-19"))
        assertThat(found.valueSetEntry.lang, equalTo("en"))
        assertThat(found.valueSetEntry.active, equalTo(true))
        assertThat(found.valueSetEntry.version, equalTo("http://snomed.info/sct/900000000000207008/version/20210131"))
        assertThat(found.valueSetEntry.system, equalTo("http://snomed.info/sct"))
    }

    @Test
    fun testAccessByCategoryKey() {
        val found = ValueSetHolder.INSTANCE.find("vaccines-covid-19-names", "EU/1/20/1528")
        assertThat(found.key, equalTo("EU/1/20/1528"))
        assertThat(found.valueSetEntry.display, equalTo("Comirnaty"))
        assertThat(found.valueSetEntry.lang, equalTo("en"))
        assertThat(found.valueSetEntry.active, equalTo(true))
        assertThat(found.valueSetEntry.version, equalTo(""))
        assertThat(found.valueSetEntry.system, equalTo("https://ec.europa.eu/health/documents/community-register/html/"))
    }
}