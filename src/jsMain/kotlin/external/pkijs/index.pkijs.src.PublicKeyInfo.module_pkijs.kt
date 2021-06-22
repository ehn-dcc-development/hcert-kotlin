@file:JsModule("pkijs/src/PublicKeyInfo")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.PublicKeyInfo

import Asn1js.BitString
import tsstdlib.CryptoKey
import tsstdlib.JsonWebKey

@JsName("default")
open external class PublicKeyInfo(params: Any = definedExternally) {
    open var algorithm: Any
    open var subjectPublicKey: BitString
    open var parsedKey: dynamic /* ECPublicKey | RSAPublicKey */
    open fun fromJSON(json: JsonWebKey)

    open fun importKey(publicKey: CryptoKey): dynamic // PromiseLike<Unit>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}