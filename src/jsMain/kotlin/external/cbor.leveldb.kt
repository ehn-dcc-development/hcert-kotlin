@file:JsQualifier("leveldb")
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package leveldb

external var decode: Any

external var encode: Any

external var buffer: Boolean

external var name: String /* "cbor" */