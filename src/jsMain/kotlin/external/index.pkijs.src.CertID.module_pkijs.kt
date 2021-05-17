@file:JsModule("pkijs/src/CertID")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.CertID

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
import Asn1js.OctetString
import Asn1js.Integer
import pkijs.src.Certificate.Certificate
import tsstdlib.PromiseLike

external interface CreateFroCertificateParams {
    var hashAlgorithm: String
    var issuerCertificate: Any
}

@JsName("default")
external open class CertID(params: Any = definedExternally) {
    open var hashAlgorithm: Any
    open var issuerNameHash: OctetString
    open var issuerKeyHash: OctetString
    open var serialNumber: Integer
    open fun isEqual(certificateID: CertID): Boolean
    open fun createForCertificate(certificate: Certificate, parameters: CreateFroCertificateParams): PromiseLike<Unit>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}