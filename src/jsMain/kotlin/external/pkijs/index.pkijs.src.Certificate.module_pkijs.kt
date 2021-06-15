@file:JsModule("pkijs/src/Certificate")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.Certificate

//import tsstdlib.PromiseLike
import Asn1js.BitString
import Asn1js.Integer
import Asn1js.Sequence
import org.khronos.webgl.ArrayBuffer
import pkijs.src.Extension.Extension
import tsstdlib.CryptoKey

@JsName("default")
open external class Certificate(params: Any = definedExternally) {
    open var tbs: ArrayBuffer
    open var version: Number
    open var serialNumber: Integer
    open var signature: Any
    open var issuer: Any
    open var notBefore: Any
    open var notAfter: Any
    open var subject: Any
    open var subjectPublicKeyInfo: Any
    open var issuerUniqueID: ArrayBuffer
    open var subjectUniqueID: ArrayBuffer
    open var extensions: Array<Extension>
    open var signatureAlgorithm: Any
    open var signatureValue: BitString
    open fun toSchema(encodeFlag: Boolean = definedExternally): Any
    open fun encodeTBS(): Sequence
    open fun getPublicKey(parameters: Any = definedExternally): dynamic // PromiseLike<CryptoKey>
    open fun getKeyHash(): dynamic // PromiseLike<ArrayBuffer>
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): dynamic // PromiseLike<ArrayBuffer>
    open fun verify(issuerCertificate: Certificate = definedExternally): dynamic //PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}