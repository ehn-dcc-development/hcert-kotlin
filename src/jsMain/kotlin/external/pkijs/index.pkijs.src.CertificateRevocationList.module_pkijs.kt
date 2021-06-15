@file:JsModule("pkijs/src/CertificateRevocationList")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.CertificateRevocationList

import Asn1js.BitString
import Asn1js.Sequence
import org.khronos.webgl.ArrayBuffer
import pkijs.src.Certificate.Certificate
import pkijs.src.RevokedCertificate.RevokedCertificate
import tsstdlib.CryptoKey

external interface `T$2` {
    var issuerCertificate: Any?
        get() = definedExternally
        set(value) = definedExternally
    var publicKeyInfo: Any?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
open external class CertificateRevocationList(params: Any = definedExternally) {
    open var tbs: ArrayBuffer
    open var version: Number
    open var signature: dynamic /* String | Algorithm */
    open var issuer: Any
    open var thisUpdate: Any
    open var nextUpdate: Any
    open var revokedCertificates: Array<RevokedCertificate>
    open var crlExtensions: Any
    open var signatureAlgorithm: dynamic /* String | Algorithm */
    open var signatureValue: BitString
    open fun toSchema(encodeFlag: Boolean = definedExternally): Any
    open fun encodeTBS(): Sequence
    open fun isCertificateRevoked(certificate: Certificate): Boolean
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): dynamic // PromiseLike<ArrayBuffer>
    open fun verify(parameters: `T$2`): dynamic// PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}