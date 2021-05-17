@file:JsModule("pkijs/src/CertificateChainValidationEngine")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.CertificateChainValidationEngine

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
import pkijs.src.Certificate.Certificate
import pkijs.src.CertificateRevocationList.CertificateRevocationList
import tsstdlib.PromiseLike

@JsName("default")
external open class CertificateChainValidationEngine(parameters: Any = definedExternally) {
    open var trustedCerts: Array<Certificate>
    open var certs: Array<Certificate>
    open var crls: Array<CertificateRevocationList>
    open var ocsp: Any
    open var checkDate: Date
    open fun sort(): Any
    open fun verify(parameters: Any = definedExternally): PromiseLike<Any>
}