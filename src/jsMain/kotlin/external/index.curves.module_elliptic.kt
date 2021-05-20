@file:JsQualifier("curves")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package curves

import kotlin.js.*

external open class PresetCurve(options: Options) {
    open var type: String
    open var g: Any
    open var n: Any?
    open var hash: Any
    interface Options {
        var type: String
        var prime: String?
        var p: String
        var a: String
        var b: String
        var n: String
        var hash: Any
        var gRed: Boolean
        var g: Any
        var beta: String?
            get() = definedExternally
            set(value) = definedExternally
        var lambda: String?
            get() = definedExternally
            set(value) = definedExternally
        var basis: Any?
            get() = definedExternally
            set(value) = definedExternally
    }
}