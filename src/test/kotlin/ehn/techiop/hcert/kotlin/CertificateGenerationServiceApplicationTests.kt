package ehn.techiop.hcert.kotlin

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class CertificateGenerationServiceApplicationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun modelIndex() {
        mockMvc.get("/").andExpect {
            status { isOk() }
            content { contentType("text/html;charset=UTF-8") }
            model { hasNoErrors() }
        }
    }

    @Test
    fun modelGenerateCOSE() {
        val mvcResult = mockMvc.post("/generate") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            param("vaccinationData", Input.pastInfectedJson)
        }.andExpect {
            status { isOk() }
            content { contentType("text/html;charset=UTF-8") }
            model {
                attributeExists("vaccinationData")
                attributeExists("cardViewModels")
            }
        }.andReturn()
        val cardViewModels = extractCardViewModels(mvcResult)
        assertNotNull(cardViewModels.find { it.title == "COSE" })
    }

    private fun extractCardViewModels(mvcResult: MvcResult): MutableList<CardViewModel> {
        val cardViewModels = mvcResult.modelAndView?.modelMap?.getAttribute("cardViewModels") as List<*>
        assertNotNull(cardViewModels)
        val result = mutableListOf<CardViewModel>()
        for (cardViewModel in cardViewModels) {
            assertNotNull(cardViewModel as CardViewModel)
            result.add(cardViewModel)
        }
        return result
    }

}
