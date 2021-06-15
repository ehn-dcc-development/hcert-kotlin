@file:JsModule("pkijs/src/AuthorityKeyIdentifier")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.AuthorityKeyIdentifier

import Asn1js.Integer
import Asn1js.OctetString
import pkijs.src.GeneralName.GeneralName

@JsName("default")
open external class AuthorityKeyIdentifier(params: Any = definedExternally) {
    open var keyIdentifier: OctetString
    open var authorityCertIssuer: Array<GeneralName>
    open var authorityCertSerialNumber: Integer
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}