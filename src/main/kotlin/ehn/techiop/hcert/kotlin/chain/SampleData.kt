package ehn.techiop.hcert.kotlin.chain

class SampleData {

    companion object {
        val recovery = """
        {
            "sub": {
                "gn": "Gabriele",
                "fn": "Musterfrau",
                "id": [
                    {
                        "t": "PP",
                        "i": "12345ABC-321",
                        "c": "AT"
                    }
                ],
                "dob": "1998-02-26"
            },
            "rec": [
                {
                    "dis": "840539006",
                    "dat": "2021-02-20",
                    "cou": "AT"
                }
            ],
            "v": "v1.0.0",
            "dgcid": "01AT42196560275230427402470256520250042"
        }
        """.trimIndent()
        val vaccination = """
        {
            "sub": {
                "gn": "Gabriele",
                "fn": "Musterfrau",
                "id": [
                    {
                        "t": "PP",
                        "i": "12345ABC-321",
                        "c": "AT"
                    }
                ],
                "dob": "1998-02-26"
            },
            "vac": [
                {
                    "dis": "840539006",
                    "vap": "1119305005",
                    "mep": "EU\/1\/20\/1528",
                    "aut": "ORG-100030215",
                    "seq": 1,
                    "tot": 2,
                    "dat": "2021-02-18",
                    "cou": "AT",
                    "lot": "C22-862FF-001",
                    "adm": "Vaccination centre Vienna 23"
                },
                {
                    "dis": "840539006",
                    "vap": "1119305005",
                    "mep": "EU\/1\/20\/1528",
                    "aut": "ORG-100030215",
                    "seq": 2,
                    "tot": 2,
                    "dat": "2021-03-12",
                    "cou": "AT",
                    "lot": "C22-H62FF-010",
                    "adm": "Vaccination centre Vienna 23"
                }
            ],
            "v": "v1.0.0",
            "dgcid": "01AT42196560275230427402470256520250042"
        }
        """.trimIndent()
        val test = """
        {
            "sub": {
                "gn": "Gabriele",
                "fn": "Musterfrau",
                "id": [
                    {
                        "t": "PP",
                        "i": "12345ABC-321",
                        "c": "AT"
                    }
                ],
                "dob": "1998-02-26"
            },
            "tst": [
                {
                    "dis": "840539006",
                    "typ": "LP6464-4",
                    "tma": "tbd tbd tbd",
                    "ori": "258500001",
                    "dts": "441759600",
                    "dtr": "441759600",
                    "res": "1240591000000102",
                    "fac": "Testing center Vienna 1",
                    "cou": "AT"
                }
            ],
            "v": "v1.0.0",
            "dgcid": "01AT42196560275230427402470256520250042"
        }
        """.trimIndent()
    }

}
