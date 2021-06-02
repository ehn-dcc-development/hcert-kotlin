@file:JsModule("pkijs/src/RelativeDistinguishedNames")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.RelativeDistinguishedNames

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
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue

@JsName("default")
external open class RelativeDistinguishedNames(params: Any = definedExternally) {
    open var typesAndValues: Array<AttributeTypeAndValue>
    open var valueBeforeDecode: ArrayBuffer
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any
    open fun isEqual(compareTo: RelativeDistinguishedNames): Boolean
    open fun isEqual(compareTo: ArrayBuffer): Boolean

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}