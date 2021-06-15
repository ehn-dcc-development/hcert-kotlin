@file:JsModule("pkijs/src/AttributeTypeAndValue")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.AttributeTypeAndValue

import Asn1js.ObjectIdentifier
import org.khronos.webgl.ArrayBuffer

@JsName("default")
open external class AttributeTypeAndValue(params: Any = definedExternally) {
    open var type: ObjectIdentifier
    open var value: Any
    open fun isEqual(compareTo: AttributeTypeAndValue): Boolean
    open fun isEqual(compareTo: ArrayBuffer): Boolean
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}