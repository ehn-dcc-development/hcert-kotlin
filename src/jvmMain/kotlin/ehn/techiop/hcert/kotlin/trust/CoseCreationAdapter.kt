package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual class CoseCreationAdapter actual constructor(private val content: ByteArray) {

    private val sign1Message = Sign1Message().also { it.SetContent(content) }

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    actual fun addProtectedAttribute(key: CoseHeaderKeys, value: Any) {
        val content = if (value is CwtAlgorithm) value.intVal else value
        sign1Message.addAttribute(
            CBORObject.FromObject(key.intVal),
            CBORObject.FromObject(content),
            Attribute.PROTECTED
        )
    }

    actual fun sign(key: PrivKey<*>) {
        sign1Message.sign(key.toCoseRepresentation() as OneKey)
    }

    actual fun encode() = sign1Message.EncodeToBytes()

}