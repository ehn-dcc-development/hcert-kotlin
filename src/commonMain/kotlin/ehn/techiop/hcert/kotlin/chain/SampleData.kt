package ehn.techiop.hcert.kotlin.chain

class SampleData {

    companion object {
        val recovery = """
        {
            "ver": "1.0.0",
            "nam": {
                "fn": "Musterfrau-G\u00f6\u00dfinger",
                "gn": "Gabriele",
                "fnt": "MUSTERFRAU<GOESSINGER",
                "gnt": "GABRIELE"
            },
            "dob": "1998-02-26",
            "r": [
                {
                    "tg": "840539006",
                    "fr": "2021-02-20",
                    "co": "AT",
                    "is": "BMSGPK Austria",
                    "df": "2021-04-04",
                    "du": "2021-10-04",
                    "ci": "urn:uvci:01:AT:858CC18CFCF5965EF82F60E493349AA5Y"
                }
            ]
        }
        """.trimIndent()
        val vaccination = """
        {
            "ver": "1.0.0",
            "nam": {
                "fn": "Musterfrau-G\u00f6\u00dfinger",
                "gn": "Gabriele",
                "fnt": "MUSTERFRAU<GOESSINGER",
                "gnt": "GABRIELE"
            },
            "dob": "1998-02-26",
            "v": [
                {
                    "tg": "840539006",
                    "vp": "1119305005",
                    "mp": "EU\/1\/20\/1528",
                    "ma": "ORG-100030215",
                    "dn": 1,
                    "sd": 2,
                    "dt": "2021-02-18",
                    "co": "AT",
                    "is": "BMSGPK Austria",
                    "ci": "urn:uvci:01:AT:10807843F94AEE0EE5093FBC254BD813P"
                }
            ]
        }
        """.trimIndent()
        val testRat = """
        {
            "ver": "1.0.0",
            "nam": {
                "fn": "Musterfrau-G\u00f6\u00dfinger",
                "gn": "Gabriele",
                "fnt": "MUSTERFRAU<GOESSINGER",
                "gnt": "GABRIELE"
            },
            "dob": "1998-02-26",
            "t": [
                {
                    "tg": "840539006",
                    "tt": "LP6464-4",
                    "ma": "1232",
                    "sc": "2021-02-20T12:34:56+00:00",
                    "dr": "2021-02-20T12:45:01+00:00",
                    "tr": "260415000",
                    "tc": "Testing center Vienna 1",
                    "co": "AT",
                    "is": "BMSGPK Austria",
                    "ci": "urn:uvci:01:AT:71EE2559DE38C6BF7304FB65A1A451ECE"
                }
            ]
        }
        """.trimIndent()
        val testNaa = """
        {
            "ver": "1.0.0",
            "nam": {
                "fn": "Musterfrau-G\u00f6\u00dfinger",
                "gn": "Gabriele",
                "fnt": "MUSTERFRAU<GOESSINGER",
                "gnt": "GABRIELE"
            },
            "dob": "1998-02-26",
            "t": [
                {
                    "tg": "840539006",
                    "tt": "LP6464-4",
                    "nm": "Roche LightCycler qPCR",
                    "sc": "2021-02-20T12:34:56+00:00",
                    "dr": "2021-02-20T14:56:01+00:00",
                    "tr": "260415000",
                    "tc": "Testing center Vienna 1",
                    "co": "AT",
                    "is": "BMSGPK Austria",
                    "ci": "urn:uvci:01:AT:B5921A35D6A0D696421B3E24621782971"
                }
            ]
        }
        """.trimIndent()
    }

}
