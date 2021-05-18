package ehn.techiop.hcert.kotlin.data

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

class ValueSetHolderTest {

    @Test
    fun testAccessByKey() {
        val found = ValueSetsInstanceHolder.INSTANCE.find("840539006")

        MatcherAssert.assertThat(found.key, CoreMatchers.equalTo("840539006"))
        MatcherAssert.assertThat(found.valueSetEntry.display, CoreMatchers.equalTo("COVID-19"))
        MatcherAssert.assertThat(found.valueSetEntry.lang, CoreMatchers.equalTo("en"))
        MatcherAssert.assertThat(found.valueSetEntry.active, CoreMatchers.equalTo(true))
        MatcherAssert.assertThat(
            found.valueSetEntry.version,
            CoreMatchers.equalTo("http://snomed.info/sct/900000000000207008/version/20210131")
        )
        MatcherAssert.assertThat(found.valueSetEntry.system, CoreMatchers.equalTo("http://snomed.info/sct"))
    }

    @Test
    fun testAccessByCategoryKey() {
        val found = ValueSetsInstanceHolder.INSTANCE.find("vaccines-covid-19-names", "EU/1/20/1528")
        MatcherAssert.assertThat(found.key, CoreMatchers.equalTo("EU/1/20/1528"))
        MatcherAssert.assertThat(found.valueSetEntry.display, CoreMatchers.equalTo("Comirnaty"))
        MatcherAssert.assertThat(found.valueSetEntry.lang, CoreMatchers.equalTo("en"))
        MatcherAssert.assertThat(found.valueSetEntry.active, CoreMatchers.equalTo(true))
        MatcherAssert.assertThat(found.valueSetEntry.version, CoreMatchers.equalTo(""))
        MatcherAssert.assertThat(
            found.valueSetEntry.system,
            CoreMatchers.equalTo("https://ec.europa.eu/health/documents/community-register/html/")
        )
    }
}