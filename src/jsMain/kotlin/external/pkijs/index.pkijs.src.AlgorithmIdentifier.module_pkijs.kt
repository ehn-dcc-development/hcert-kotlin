@file:JsModule("pkijs/src/AlgorithmIdentifier")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.AlgorithmIdentifier

@JsName("default")
open external class AlgorithmIdentifier(params: Any = definedExternally) {
    open var algorithmId: String
    open var algorithmParams: Any
    open fun isEqual(algorithmIdentifier: AlgorithmIdentifier): Boolean
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}