@file:JsModule("pkijs/src/BasicOCSPResponse")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.BasicOCSPResponse

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
import pkijs.src.Certificate.Certificate
import tsstdlib.PromiseLike
import tsstdlib.CryptoKey

external interface GetCertificateStatusResult {
    var isForCertificate: Boolean
    var status: Number
}

external interface `T$1` {
    var trustedCerts: Array<Certificate>?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
external open class BasicOCSPResponse(params: Any = definedExternally) {
    open var tbsResponseData: Any
    open var signatureAlgorithm: Any
    open var signature: BitString
    open var certs: Array<Certificate>
    open fun getCertificateStatus(certificate: Certificate, issuerCertificate: Certificate): PromiseLike<GetCertificateStatusResult>
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): PromiseLike<ArrayBuffer>
    open fun verify(parameters: `T$1` = definedExternally): PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}