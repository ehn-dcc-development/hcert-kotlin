@file:JsModule("pkijs/src/RevokedCertificate")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.RevokedCertificate

import Asn1js.Integer

@JsName("default")
open external class RevokedCertificate(params: Any = definedExternally) {
    open var userCertificate: Integer
    open var revocationDate: Any
    open var crlEntryExtensions: Any
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}