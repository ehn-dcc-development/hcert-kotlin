package ehn.techiop.hcert.kotlin.chain

import com.google.zxing.BarcodeFormat
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TwoDimCodeServiceTest {

    @Test
    fun dummyTest() {
        val size = 350
        val service = TwoDimCodeService(size, BarcodeFormat.QR_CODE)

        val encoded = service.encode("foo")

        assertNotNull(encoded)
        assertThat(encoded.length, greaterThan(size))
    }

}