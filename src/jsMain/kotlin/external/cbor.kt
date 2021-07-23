@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "DEPRECATION"
)
@file:JsModule("cbor")
@file:JsNonModule

package Cbor

import Buffer
import org.khronos.webgl.ArrayBufferView
import tsstdlib.Iterable
import kotlin.js.Promise

external interface `T$1` {

    @nativeGetter
    operator fun get(tag: Number): ((v: Any) -> Any)?

    @nativeSetter
    operator fun set(tag: Number, value: (v: Any) -> Any)
}

external interface DecoderOptions {
    var max_depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tags: `T$1`?
        get() = definedExternally
        set(value) = definedExternally
    var bigint: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Map(iterable: Iterable<Any> = definedExternally) {

    fun get(key: dynamic): dynamic
    fun set(key: dynamic, `val`: dynamic): dynamic
}

open external class Decoder(options: DecoderOptions = definedExternally) {
    companion object {
        fun nullcheck(param_val: Any): Any
        fun decodeFirstSync(input: String, options: DecoderOptions = definedExternally): Any
        fun decodeFirstSync(input: String, options: String = definedExternally): Any
        fun decodeFirstSync(input: Buffer, options: DecoderOptions = definedExternally): Any
        fun decodeFirstSync(input: Buffer, options: String = definedExternally): Any
        fun decodeFirstSync(input: ArrayBufferView, options: DecoderOptions = definedExternally): Any
        fun decodeFirstSync(input: ArrayBufferView, options: String = definedExternally): Any
        fun decodeAllSync(input: String, options: DecoderOptions = definedExternally): Array<Any>
        fun decodeAllSync(input: String, options: String = definedExternally): Array<Any>
        fun decodeAllSync(input: Buffer, options: DecoderOptions = definedExternally): Array<Any>

        /*fun decodeAllSync(input: Buffer, options: String = definedExternally): Array<Any>
        fun decodeAllSync(input: ArrayBufferView, options: DecoderOptions = definedExternally): Array<Any>
        fun decodeAllSync(input: ArrayBufferView, options: String = definedExternally): Array<Any>*/
        fun decodeFirst(input: String, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: Buffer, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: ArrayBufferView, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: String, options: DecoderOptions, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: String, options: String, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: Buffer, options: DecoderOptions, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: Buffer, options: String, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: ArrayBufferView, options: DecoderOptions, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: ArrayBufferView, options: String, cb: (error: Error, value: Any) -> Unit)
        fun decodeFirst(input: String, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeFirst(input: String, options: String = definedExternally): Promise<Any>
        fun decodeFirst(input: Buffer, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeFirst(input: Buffer, options: String = definedExternally): Promise<Any>
        fun decodeFirst(input: ArrayBufferView, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeFirst(input: ArrayBufferView, options: String = definedExternally): Promise<Any>
        fun decodeAll(input: String, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: Buffer, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: ArrayBufferView, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: String, options: DecoderOptions, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: String, options: String, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: Buffer, options: DecoderOptions, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: Buffer, options: String, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: ArrayBufferView, options: DecoderOptions, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: ArrayBufferView, options: String, cb: (error: Error, value: Array<Any>) -> Unit)
        fun decodeAll(input: String, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeAll(input: String, options: String = definedExternally): Promise<Any>
        fun decodeAll(input: Buffer, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeAll(input: Buffer, options: String = definedExternally): Promise<Any>
        fun decodeAll(input: ArrayBufferView, options: DecoderOptions = definedExternally): Promise<Any>
        fun decodeAll(input: ArrayBufferView, options: String = definedExternally): Promise<Any>
    }
}

external interface EncoderOptions {
    var genTypes: Array<Any>?
        get() = definedExternally
        set(value) = definedExternally
    var canonical: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var detectLoops: dynamic /* Boolean? | Any? */
        get() = definedExternally
        set(value) = definedExternally
    var dateType: String? /* "number" | "float" | "int" | "string" */
        get() = definedExternally
        set(value) = definedExternally
    var encodeUndefined: Any?
        get() = definedExternally
        set(value) = definedExternally
    var disallowUndefinedKeys: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Encoder(options: EncoderOptions = definedExternally) {
    open fun <T> addSemanticType(
        type: Any,
        encodeFunction: (encoder: Encoder, t: T) -> Boolean
    ): (encoder: Encoder, t: T) -> Boolean?

    open fun pushAny(input: Any): Boolean
    open fun removeLoopDetectors(obj: Any): Boolean

    companion object {
        fun encode(vararg objs: Any): Buffer /* Buffer | ArrayBufferView */
        fun encodeCanonical(vararg objs: Any): Buffer /* Buffer | ArrayBufferView */
        fun encodeOne(obj: Any, options: EncoderOptions = definedExternally): Buffer /* Buffer | ArrayBufferView */
        fun encodeAsync(
            obj: Any,
            options: EncoderOptions = definedExternally
        ): Promise<dynamic /* Buffer | ArrayBufferView */>
    }
}

open external class Simple(value: Number) {
    override fun toString(): String
    open fun encodeCBOR(gen: Encoder): Boolean

    companion object {
        fun isSimple(obj: Any): Boolean
        fun decode(
            param_val: Number,
            has_parent: Boolean = definedExternally,
            parent_indefinite: Boolean = definedExternally
        ): dynamic /* Boolean? | Any? | Simple? */
    }
}

external interface CommentedOptions {
    var max_depth: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CommentOptions {
    var max_depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Commented(options: CommentedOptions = definedExternally) {
    companion object {
        fun comment(input: String, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: Buffer, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: ArrayBufferView, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: String, options: CommentOptions, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: String, options: String, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: Buffer, options: CommentOptions, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: Buffer, options: String, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: ArrayBufferView, options: CommentOptions, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: ArrayBufferView, options: String, cb: (error: Error, commented: String) -> Unit)
        fun comment(input: String, options: CommentOptions = definedExternally): Promise<String>
        fun comment(input: String, options: String = definedExternally): Promise<String>
        fun comment(input: Buffer, options: CommentOptions = definedExternally): Promise<String>
        fun comment(input: Buffer, options: String = definedExternally): Promise<String>
        fun comment(input: ArrayBufferView, options: CommentOptions = definedExternally): Promise<String>
        fun comment(input: ArrayBufferView, options: String = definedExternally): Promise<String>
    }
}

open external class Tagged(tag: Number, value: Any = definedExternally, err: Error = definedExternally) {
    override fun toString(): String
    open fun encodeCBOR(gen: Encoder): Boolean
    open fun convert(converters: `T$1` = definedExternally): Any
    var value: Any
}

external interface DiagnoseOptions {
    var separator: String?
        get() = definedExternally
        set(value) = definedExternally
    var stream_errors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var max_depth: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Diagnose(options: DiagnoseOptions = definedExternally) {
    companion object {
        fun diagnose(input: String, encoding: String = definedExternally): Promise<String>
        fun diagnose(input: Buffer, encoding: String = definedExternally): Promise<String>
        fun diagnose(input: ArrayBufferView, encoding: String = definedExternally): Promise<String>
        fun diagnose(input: String, cb: (error: Error, str: String) -> Unit)
        fun diagnose(input: Buffer, cb: (error: Error, str: String) -> Unit)
        fun diagnose(input: ArrayBufferView, cb: (error: Error, str: String) -> Unit)
    }
}

external var decode: Any

external var decodeFirstSync: Any

external var decodeAllSync: Any

external var decodeFirst: Any

external var decodeAll: Any

external var encode: Any

external var encodeCanonical: Any

external var encodeOne: Any

external var encodeAsync: Any

external var comment: Any