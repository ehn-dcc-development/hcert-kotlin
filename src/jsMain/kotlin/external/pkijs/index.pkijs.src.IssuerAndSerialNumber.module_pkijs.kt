@file:JsModule("pkijs/src/IssuerAndSerialNumber")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.IssuerAndSerialNumber

import Asn1js.Integer

@JsName("default")
open external class IssuerAndSerialNumber(params: Any = definedExternally) {
    open var issuer: Any
    open var serialNumber: Integer
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}