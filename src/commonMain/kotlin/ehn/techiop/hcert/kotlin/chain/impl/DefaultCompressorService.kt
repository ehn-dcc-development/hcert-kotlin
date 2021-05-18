package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService

expect class DefaultCompressorService(level: Int = 9) : CompressorService {

}