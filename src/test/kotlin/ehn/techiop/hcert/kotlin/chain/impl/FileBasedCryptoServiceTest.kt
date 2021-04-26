package ehn.techiop.hcert.kotlin.chain.impl

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FileBasedCryptoServiceTest {

    @ParameterizedTest
    @ValueSource(ints = [256, 384, 521])
    fun testEcImport(keySize: Int) {
        val input = RandomEcKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        assertArrayEquals(input.getCborSigningKey().EncodeToBytes(), parsed.getCborSigningKey().EncodeToBytes())
        assertEquals(input.getCertificate(), parsed.getCertificate())
    }

    @ParameterizedTest
    @ValueSource(ints = [2048, 3072])
    fun testRsaImport(keySize: Int) {
        val input = RandomRsaKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        assertArrayEquals(input.getCborSigningKey().EncodeToBytes(), parsed.getCborSigningKey().EncodeToBytes())
        assertEquals(input.getCertificate(), parsed.getCertificate())
    }

}