@file:JsModule("pkijs/src/IssuingDistributionPoint")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.IssuingDistributionPoint

@JsName("default")
open external class IssuingDistributionPoint(params: Any = definedExternally) {
    open var distributionPoint: dynamic /* Array<GeneralName> | RelativeDistinguishedNames */
    open var onlyContainsUserCerts: Boolean
    open var onlySomeReasons: Number
    open var indirectCRL: Boolean
    open var onlyContainsAttributeCerts: Boolean
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}