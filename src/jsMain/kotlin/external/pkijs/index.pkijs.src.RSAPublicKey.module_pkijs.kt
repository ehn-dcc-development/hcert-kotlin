@file:JsModule("pkijs/src/RSAPublicKey")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.RSAPublicKey

import Asn1js.Integer
import tsstdlib.JsonWebKey

@JsName("default")
open external class RSAPublicKey(params: Any = definedExternally) {
    open var modulus: Integer
    open var publicExponent: Integer
    open fun fromJSON(json: JsonWebKey)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}