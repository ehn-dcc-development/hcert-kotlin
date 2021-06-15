@file:JsModule("pkijs/src/ECPrivateKey")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.ECPrivateKey

import Asn1js.OctetString
import tsstdlib.JsonWebKey

@JsName("default")
open external class ECPrivateKey(params: Any = definedExternally) {
    open var version: Number
    open var privateKey: OctetString
    open var namedCurve: String
    open var publicKey: Any
    open fun fromJSON(json: JsonWebKey)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}