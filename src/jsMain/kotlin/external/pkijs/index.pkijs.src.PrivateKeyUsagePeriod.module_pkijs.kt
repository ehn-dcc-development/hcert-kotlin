@file:JsModule("pkijs/src/PrivateKeyUsagePeriod")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.PrivateKeyUsagePeriod

import kotlin.js.Date

@JsName("default")
open external class PrivateKeyUsagePeriod(params: Any = definedExternally) {
    open var notBefore: Date
    open var notAfter: Date
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}