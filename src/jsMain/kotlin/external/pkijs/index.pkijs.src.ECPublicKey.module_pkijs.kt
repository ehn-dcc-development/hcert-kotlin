@file:JsModule("pkijs/src/ECPublicKey")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.ECPublicKey

import org.khronos.webgl.ArrayBuffer
import tsstdlib.JsonWebKey

@JsName("default")
open external class ECPublicKey(params: Any = definedExternally) {
    open var x: ArrayBuffer
    open var y: ArrayBuffer
    open var namedCurve: String
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