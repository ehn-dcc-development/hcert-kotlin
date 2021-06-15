@file:JsModule("pkijs/src/CRLDistributionPoints")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.CRLDistributionPoints

import pkijs.src.DistributionPoint.DistributionPoint

@JsName("default")
open external class CRLDistributionPoints(params: Any = definedExternally) {
    open var distributionPoints: Array<DistributionPoint>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}