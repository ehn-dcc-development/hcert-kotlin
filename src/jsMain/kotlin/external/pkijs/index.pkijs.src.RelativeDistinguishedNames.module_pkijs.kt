@file:JsModule("pkijs/src/RelativeDistinguishedNames")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.RelativeDistinguishedNames

import org.khronos.webgl.ArrayBuffer
import pkijs.src.AttributeTypeAndValue.AttributeTypeAndValue

@JsName("default")
open external class RelativeDistinguishedNames(params: Any = definedExternally) {
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