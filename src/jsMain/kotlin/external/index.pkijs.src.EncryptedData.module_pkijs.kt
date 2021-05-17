@file:JsModule("pkijs/src/EncryptedData")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.EncryptedData

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
import pkijs.src.Attribute.Attribute
import tsstdlib.Algorithm
import tsstdlib.PromiseLike

external interface `T$3` {
    var password: String
    var contentEncryptionAlgorithm: Algorithm
    var hmacHashAlgorithm: String
    var iterationCount: Number
    var contentToEncrypt: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$4` {
    var password: String
}

@JsName("default")
external open class EncryptedData(params: Any = definedExternally) {
    open var version: Number
    open var encryptedContentInfo: Any
    open var unprotectedAttrs: Array<Attribute>
    open fun encrypt(parameters: `T$3`): PromiseLike<ArrayBuffer>
    open fun decrypt(parameters: `T$4`): PromiseLike<ArrayBuffer>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}