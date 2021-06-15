@file:JsModule("pkijs/src/Extension")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.Extension

import Asn1js.OctetString

@JsName("default")
open external class Extension(params: Any = definedExternally) {
    open var extnID: String
    open var critical: Boolean
    open var extnValue: OctetString
    open var parsedValue: Any
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}