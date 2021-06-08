package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.ext.FixedClock
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.crypto.Certificate
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TrustListTest : io.kotest.core.spec.style.StringSpec({

    "V2 Client-Server Exchange" {
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val certificate = cryptoService.getCertificate().encoded.asBase64()
        val encodeService = TrustListV2EncodeService(cryptoService, clock = clock)
        val trustListEncoded = encodeService.encodeContent(randomCertificates(clock))
        val trustListSignature = encodeService.encodeSignature(trustListEncoded)

        verifyClientOperations(certificate, clock, trustListSignature, trustListEncoded)
    }

    "V2 Client Loading" {
        val certificate =
            "MIIBWzCCAQCgAwIBAgIFAN9xteowCgYIKoZIzj0EAwIwEDEOMAwGA1UEAwwFRUMtTWUwHhcNNzAwMTAxMDAwMDAwWhcNNzAwMTMxMDAwMDAwWjAQMQ4wDAYDVQQDDAVFQy1NZTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABJBORxfDtK/ajXOa08zXn5mCKfLXPr5IFhB6PDUfGnjIuUbBigVA1ksVFGyORgSt8dwnT7rOSJXcs2Rlkx8nQ9SjRzBFMA4GA1UdDwEB/wQEAwIFoDAzBgNVHSUELDAqBgwrBgEEAQCON49lAQEGDCsGAQQBAI43j2UBAgYMKwYBBAEAjjePZQEDMAoGCCqGSM49BAMCA0kAMEYCIQDV+bG2GM4NCNCMRcDCiOAKU0U8crilqkmypalSu4ciRgIhAJ480iC1krl87fY0EWTC7v0UJpxxmoHCuqQOLB81Ew7f"
        val trustListEncoded =
            "bf61639fbf61694898d88bbda093b1f8616359015e3082015a30820100a003020102020500862d6d5a300a06082a8648ce3d0403023010310e300c06035504030c0545432d4d65301e170d3730303130313030303030305a170d3730303133313030303030305a3010310e300c06035504030c0545432d4d653059301306072a8648ce3d020106082a8648ce3d0301070342000453db8a15f340804811db6ec759c2cd370864760991a93d280cac8baabf9dd352bc8601916b3586d7b8f659dd5f4853d3552322b253c2bdad2fc7d6ddc77030b1a3473045300e0603551d0f0101ff0404030205a030330603551d25042c302a060c2b06010401008e378f650101060c2b06010401008e378f650102060c2b06010401008e378f650103300a06082a8648ce3d0403020348003045022100a7adde022495ef6028369635129fb0af2ec6852b37706c2cbef40d8d79c578e90220102cd75743f3bd67133cb4964a327800698c9c76c31a4f92694e509761bd2a81ffbf616948cb14342a14c1f46561635902eb308202e7308201cfa00302010202047e040195300d06092a864886f70d01010b05003011310f300d06035504030c065253412d4d65301e170d3730303130313030303030305a170d3730303133313030303030305a3011310f300d06035504030c065253412d4d6530820122300d06092a864886f70d01010105000382010f003082010a0282010100bfe02fa09c926cb214a5040c3e46b627eef7100508e391daa193fbdfbd9c0b4f46d2678dd9c1f8f12989ac583076e73b48fbfc5dc99618dc78f071a49ba86b8d078417f4c12c98843ccf2432e6ae14058ec29ef4696e1bf1f1a87f914a31f634972cc59d7645c7d927f40a0a9488cf312a0fe3a0b49dbc2af457b89719bd4db7f35b0b0dd0c30dc3007efae3bc895dcc825ac7a12d76abedbe04da0b201238a8059007db6d0c123c285cba4688a981e2ebb1558c696eff3cfe4db97f43b6bbaa7a1bda26f53aac488141c7173dcc21baabbcd0640b266e22b9d91efc534401747f9f820a839f3c16450e29c9be186735f9add8e929f3b8c3cf82714c9dc23bd50203010001a3473045300e0603551d0f0101ff0404030205a030330603551d25042c302a060c2b06010401008e378f650101060c2b06010401008e378f650102060c2b06010401008e378f650103300d06092a864886f70d01010b05000382010100a56d47b8e118b8506cafba5ed5c5f151023faec1ce8ff5d968e7c80f7caebe57154206f5addd5e0e2b9ed0bd35f81d8f09497fc20f67623079f02ab09013bef387f657e39853bc4ea9ac45b0e1e81fe14d7d43adf370187c1856079888bedd06cca1ae897b6c3a4cddd3507cef0129ecb5a87d3c49391af36cf10b213e96bfd31efc1c057864eca0fc38277b564fe4ef327806267993aecb9559bc93bd8f3934b8cb15a05f19578d51852621006892175d34e261291996c6c4b3ff8beb0fc066aaf1ff40fa8ef401c9d637cc2eb4a41b8405dcd1a2d93561a4c6a06f81d04b65936f457b55baf091cb64e0204a8d122a19ccc585cb1eb108b28d48ca924916afffffff".fromHexString()
        val trustListSignature =
            "d28450a3182a0204487b400581fb6aea6e0126a0582ca3025820593fb23e0915d50be21029450e3051a026316c4826383edb184907f3e96038f7041a0002a30005005840cd0d5cba9a9c059dd25975cd4131884a7de4720957a48d88b9d8a5df8c956cf792c52811238ba49dfd9bfdbae33c4553c4c8a0536588b2c0b7fbc521a2c079b8".fromHexString()
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))

        verifyClientOperations(certificate, clock, trustListSignature, trustListEncoded)
    }


})

private fun verifyClientOperations(
    certificateBase64: String,
    clock: Clock,
    trustListSignature: ByteArray,
    trustListEncoded: ByteArray? = null
) {
    // might never happen on the client, that the trust list is loaded in this way
    val clientTrustRoot = PrefilledCertificateRepository(certificateBase64)
    val decodeService = TrustListDecodeService(clientTrustRoot, clock = clock)
    val clientTrustList = decodeService.decode(trustListSignature, trustListEncoded)
    // that's the way to go: Trust list used for verification of QR codes
    val clientTrustListAdapter =
        TrustListCertificateRepository(trustListSignature, trustListEncoded, clientTrustRoot, clock)

    clientTrustList.size shouldBe 2
    for (cert in clientTrustList) {
        cert.validFrom.epochSeconds shouldBeLessThanOrEqual clock.now().epochSeconds
        cert.validUntil.epochSeconds shouldBeGreaterThanOrEqual clock.now().epochSeconds
        cert.kid.size shouldBe 8
        cert.validContentTypes.size shouldBe 3

        clientTrustListAdapter.loadTrustedCertificates(cert.kid, VerificationResult()).forEach {
            val loadedEncoding = it.cosePublicKey.toCoseRepresentation()
            val certEncoding = cert.cosePublicKey.toCoseRepresentation()
            loadedEncoding shouldNotBe null
            certEncoding shouldNotBe null
            // TODO JVM OneKeys are not "the same"
            //loadedEncoding shouldBe certEncoding
        }
    }
}


private fun randomCertificates(clock: Clock): Set<Certificate<*>> =
    listOf(RandomEcKeyCryptoService(clock = clock), RandomEcKeyCryptoService(clock = clock))
        .map { it.getCertificate() }
        .toSet()
