@file:JsModule("pkijs/src/CertificationRequest")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.CertificationRequest

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
import pkijs.src.Attribute.Attribute
import tsstdlib.CryptoKey
import tsstdlib.PromiseLike

@JsName("default")
external open class CertificationRequest(params: Any = definedExternally) {
    open var tbs: ArrayBuffer
    open var version: Number
    open var subject: Any
    open var subjectPublicKeyInfo: Any
    open var attributes: Array<Attribute>
    open var signatureAlgorithm: Any
    open var signatureValue: BitString
    open fun toSchema(encodeFlag: Boolean = definedExternally): Any
    open fun encodeTBS(): Sequence
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): PromiseLike<ArrayBuffer>
    open fun verify(): PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}