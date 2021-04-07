package ehn.techiop.hcert.kotlin

class Input {

    companion object {
        val pastInfectedJson = """
        {
            "sub": {
                "n": "Gabi Musterfrau",
                "dob": "1999-04-20",
                "id": [{
                    "t": "pas",
                    "i": "c228f2ff"
                }]
            },
            "rec": {
                "dis": "U07.1",
                "dat": "2020-12-20",
                "cou": "AT"
            },
            "certificateMetadata": {
                "issuer": "BMGSPK, Vienna, Austria",
                "identifier": "01ATBA712FE030C797287CB97334452966470042",
                "validFrom": "2021-01-20",
                "validUntil": "2021-07-20",
                "schemaVersion": "1.0.0"
            }
        }
        """.trimIndent()
        val vaccinatedJson = """
        {
            "sub": {
                "n": "Gabi Musterfrau",
                "dob": "1999-04-20",
                "id": [{
                    "t": "pas",
                    "i": "c228f2ff"
                }]
            },
            "vac": [
                {
                    "dis": "U07.1",
                    "des": "J07BX03",
                    "nam": "COVID-19 Vaccine mRNA-1273",
                    "aut": "Pfizer BioNTech",
                    "seq": 1,
                    "tot": 2,
                    "lot": "AAZD7781-001",
                    "dat": "2021-02-22",
                    "adm": "Impfservice Town Town, Vienna",
                    "cou": "AT"
                },
                {
                    "dis": "U07.1",
                    "des": "J07BX03",
                    "nam": "COVID-19 Vaccine mRNA-1273",
                    "aut": "Pfizer BioNTech",
                    "seq": 2,
                    "tot": 2,
                    "lot": "AAZD8892-033",
                    "dat": "2021-03-16",
                    "adm": "Impfservice Town Town, Vienna",
                    "cou": "AT"
                }
            ],
            "certificateMetadata": {
                "issuer": "BMGSPK, Vienna, Austria",
                "identifier": "01ATBA712FE030C797287CB97334452966470042",
                "validFrom": "2021-01-20",
                "validUntil": "2021-07-20",
                "schemaVersion": "1.0.0"
            }
         }
        """.trimIndent()
        val testedJson = """
        {
            "sub": {
                "n": "Gabi Musterfrau",
                "dob": "1999-04-20",
                "id": [{
                    "t": "pas",
                    "i": "c228f2ff"
                }]
            },
            "tst": {
                "dis": "U07.1",
                "typ": "rapid antigen",
                "tna": "CLINITEST Rapid COVID-19 Antigen Test",
                "tma": "Siemens Healthineers",
                "ori": "",
                "dat": "2021-02-28T12:34:56+01:00",
                "res": "negative",
                "fac": "Teststrasse BMG",
                "cou": "AT"
            },
            "certificateMetadata": {
                "issuer": "BMGSPK, Vienna, Austria",
                "identifier": "01ATBA712FE030C797287CB97334452966470042",
                "validFrom": "2021-01-20",
                "validUntil": "2021-07-20",
                "validUntilextended": "2021-01-27T09:47:40.317Z",
                "revokelistidentifier": "https://qr.gv.at/irl/",
                "schemaVersion": "1.0.0"
            }
        }
        """.trimIndent()
    }

}
