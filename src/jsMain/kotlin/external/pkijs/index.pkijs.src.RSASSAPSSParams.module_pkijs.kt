@file:JsModule("pkijs/src/RSASSAPSSParams")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package pkijs.src.RSASSAPSSParams

@JsName("default")
open external class RSASSAPSSParams(params: Any = definedExternally) {
    open var hashAlgorithm: Any
    open var maskGenAlgorithm: Any
    open var saltLength: Number
    open var trailerField: Number
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}