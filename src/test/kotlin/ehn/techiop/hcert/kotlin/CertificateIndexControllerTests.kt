package ehn.techiop.hcert.kotlin

import COSE.HeaderKeys
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.security.cert.CertificateFactory

@SpringBootTest
@AutoConfigureMockMvc
class CertificateIndexControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var cryptoService: CryptoService

    private val URL_PREFIX = "/cert"

    @Test
    fun certificateAsText() {
        val kid = cryptoService.getCborHeaders().first { it.first == HeaderKeys.KID }.second.AsString()
        val certificate = mockMvc.get("$URL_PREFIX/$kid") {
            accept(MediaType.TEXT_PLAIN)
        }.andExpect {
            status { isOk() }
            content { contentType("${MediaType.TEXT_PLAIN};charset=UTF-8") }
        }.andReturn().response.contentAsString

        val parsedCertificate =
            CertificateFactory.getInstance("X.509").generateCertificate(certificate.fromBase64().inputStream())

        assertNotNull(parsedCertificate)
    }

    @Test
    fun certificateAsBinary() {
        val kid = cryptoService.getCborHeaders().first { it.first == HeaderKeys.KID }.second.AsString()
        val certificate = mockMvc.get("$URL_PREFIX/$kid") {
            accept(MediaType.APPLICATION_OCTET_STREAM)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_OCTET_STREAM) }
        }.andReturn().response.contentAsByteArray

        val parsedCertificate =
            CertificateFactory.getInstance("X.509").generateCertificate(certificate.inputStream())

        assertNotNull(parsedCertificate)
    }

}


