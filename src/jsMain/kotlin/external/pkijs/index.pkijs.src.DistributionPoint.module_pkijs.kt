@file:JsModule("pkijs/src/DistributionPoint")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.DistributionPoint

import Asn1js.BitString
import pkijs.src.GeneralName.GeneralName

@JsName("default")
open external class DistributionPoint(params: Any = definedExternally) {
    open var distributionPoint: Array<GeneralName>
    open var reasons: BitString
    open var cRLIssuer: Array<GeneralName>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}