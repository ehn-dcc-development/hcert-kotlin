@file:JsModule("pkijs/src/TSTInfo")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.TSTInfo

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
import Asn1js.Integer
import pkijs.src.Extension.Extension
import tsstdlib.PromiseLike

external interface VerifyParams {
    var data: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var notBefore: Date?
        get() = definedExternally
        set(value) = definedExternally
    var notAfter: Date?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
external open class TSTInfo(params: Any = definedExternally) {
    open var version: Number
    open var policy: String
    open var messageImprint: Any
    open var serialNumber: Integer
    open var genTime: Date
    open var accuracy: Any
    open var ordering: Boolean
    open var nonce: Integer
    open var tsa: Any
    open var extensions: Array<Extension>
    open fun verify(params: VerifyParams): PromiseLike<Boolean>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}