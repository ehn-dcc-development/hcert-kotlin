package ehn.techiop.hcert.kotlin.chain.impl

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FileBasedCryptoServiceTest {

    @ParameterizedTest
    @ValueSource(ints = [256, 384])
    fun testEcImport(keySize: Int) {
        val input = RandomEcKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        assertThat(
            input.getCborSigningKey().oneKey.EncodeToBytes(),
            equalTo(parsed.getCborSigningKey().oneKey.EncodeToBytes())
        )
        assertThat(input.getCertificate().calcKid(), equalTo(parsed.getCertificate().calcKid()))
    }

    @ParameterizedTest
    @ValueSource(ints = [2048, 3072])
    fun testRsaImport(keySize: Int) {
        val input = RandomRsaKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        assertThat(
            input.getCborSigningKey().oneKey.EncodeToBytes(),
            equalTo(parsed.getCborSigningKey().oneKey.EncodeToBytes())
        )
        assertThat(input.getCertificate().calcKid(), equalTo(parsed.getCertificate().calcKid()))
    }

}