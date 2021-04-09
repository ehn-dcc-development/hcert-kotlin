package ehn.techiop.hcert.kotlin

import COSE.HeaderKeys
import COSE.KeyKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.interfaces.ECPublicKey

class VerificationCryptoService(private val baseUrl: String) : CryptoService {

    override fun getCborHeaders() = listOf<Pair<HeaderKeys, CBORObject>>()

    override fun getCborSigningKey() = OneKey(CBORObject.NewMap())

    override fun getCborVerificationKey(kid: String): OneKey {
        val certificate = getCertificate(kid)
        val ecPublicKey = certificate.publicKey as ECPublicKey
        val bcPublicKey = ECUtil.generatePublicKeyParameter(ecPublicKey) as ECPublicKeyParameters
        return OneKey(CBORObject.NewMap().also {
            it[KeyKeys.KeyType.AsCBOR()] = KeyKeys.KeyType_EC2
            it[KeyKeys.EC2_Curve.AsCBOR()] = KeyKeys.EC2_P256
            it[KeyKeys.EC2_X.AsCBOR()] = CBORObject.FromObject((bcPublicKey).q.xCoord.encoded)
            it[KeyKeys.EC2_Y.AsCBOR()] = CBORObject.FromObject((bcPublicKey).q.yCoord.encoded)
        })
    }

    override fun getCertificate(kid: String): Certificate {
        val request = Request.Builder().get().url("$baseUrl/$kid").build()
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        response.body?.let {
            return CertificateFactory.getInstance("X.509").generateCertificate(it.byteStream())
        }
        throw IllegalArgumentException("Unable to get certificate for $kid at $baseUrl")
    }

}


