package ehn.techiop.hcert.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CertificateGenerationServiceApplication

fun main(args: Array<String>) {
	runApplication<CertificateGenerationServiceApplication>(*args)
}
