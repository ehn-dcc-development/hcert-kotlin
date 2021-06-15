@file:JsModule("pkijs/src/OtherPrimeInfo")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.OtherPrimeInfo

import Asn1js.Integer
import JsonOtherPrimeInfo

@JsName("default")
open external class OtherPrimeInfo(params: Any = definedExternally) {
    open var prime: Integer
    open var exponent: Integer
    open var coefficient: Integer
    open fun fromJSON(json: JsonOtherPrimeInfo)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}