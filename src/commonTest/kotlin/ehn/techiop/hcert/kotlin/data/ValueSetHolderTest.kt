package ehn.techiop.hcert.kotlin.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValueSetHolderTest : StringSpec({

    "Access by Key" {
        val found = ValueSetsInstanceHolder.INSTANCE.find("840539006")
        found.key shouldBe "840539006"
        found.valueSetEntry.display shouldBe "COVID-19"
        found.valueSetEntry.lang shouldBe "en"
        found.valueSetEntry.active shouldBe true
        found.valueSetEntry.version shouldBe "http://snomed.info/sct/900000000000207008/version/20210131"
        found.valueSetEntry.system shouldBe "http://snomed.info/sct"
    }

    "Access by Key with Whitespace" {
        val found = ValueSetsInstanceHolder.INSTANCE.find(" 840539006 ")
        found.key shouldBe "840539006"
        found.valueSetEntry.display shouldBe "COVID-19"
        found.valueSetEntry.lang shouldBe "en"
        found.valueSetEntry.active shouldBe true
        found.valueSetEntry.version shouldBe "http://snomed.info/sct/900000000000207008/version/20210131"
        found.valueSetEntry.system shouldBe "http://snomed.info/sct"
    }

    "Access by Category Key" {
        val found = ValueSetsInstanceHolder.INSTANCE.find("vaccines-covid-19-names", "EU/1/20/1528")
        found.key shouldBe "EU/1/20/1528"
        found.valueSetEntry.display shouldBe "Comirnaty"
        found.valueSetEntry.lang shouldBe "en"
        found.valueSetEntry.active shouldBe true
        found.valueSetEntry.version shouldBe ""
        found.valueSetEntry.system shouldBe "https://ec.europa.eu/health/documents/community-register/html/"
    }
})