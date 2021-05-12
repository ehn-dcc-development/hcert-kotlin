package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService

expect class DefaultCompressorService: CompressorService {
    companion object {
        fun getInstance(): DefaultCompressorService
    }
}