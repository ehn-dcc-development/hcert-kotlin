package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class DefaultTwoDimCodeServiceTest {

    @Test
    fun dummyTest() {
        val size = 350
        val service = DefaultTwoDimCodeService(size, BarcodeFormat.QR_CODE)

        val encoded = service.encode("foo")

        assertNotNull(encoded)
        assertThat(encoded.length, greaterThan(size))
    }

}