@file:JsModule("pkijs/src/PrivateKeyInfo")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.PrivateKeyInfo

import Asn1js.OctetString
import pkijs.src.Attribute.Attribute
import tsstdlib.JsonWebKey

@JsName("default")
open external class PrivateKeyInfo(params: Any = definedExternally) {
    open var version: Number
    open var privateKeyAlgorithm: Any
    open var privateKey: OctetString
    open var attributes: Array<Attribute>
    open var parsedKey: dynamic /* ECPrivateKey | RSAPrivateKey */
    open fun fromJSON(json: JsonWebKey)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}