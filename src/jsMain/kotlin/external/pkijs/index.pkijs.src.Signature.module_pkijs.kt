@file:JsModule("pkijs/src/Signature")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.Signature

import Asn1js.BitString
import pkijs.src.Certificate.Certificate

@JsName("default")
open external class Signature(params: Any = definedExternally) {
    open var signatureAlgorithm: Any
    open var signature: BitString
    open var certs: Array<Certificate>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}