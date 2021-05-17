@file:JsModule("pkijs/src/CertificateRevocationList")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.CertificateRevocationList

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import Asn1js.BitString
import Asn1js.Sequence
import pkijs.src.Certificate.Certificate
import pkijs.src.RevokedCertificate.RevokedCertificate
import tsstdlib.CryptoKey
import tsstdlib.PromiseLike

external interface `T$2` {
    var issuerCertificate: Any?
        get() = definedExternally
        set(value) = definedExternally
    var publicKeyInfo: Any?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
external open class CertificateRevocationList(params: Any = definedExternally) {
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
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): PromiseLike<ArrayBuffer>
    open fun verify(parameters: `T$2`): PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}