@file:JsModule("pako")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package Pako

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

external enum class FlushValues {
    Z_NO_FLUSH /* = 0 */,
    Z_PARTIAL_FLUSH /* = 1 */,
    Z_SYNC_FLUSH /* = 2 */,
    Z_FULL_FLUSH /* = 3 */,
    Z_FINISH /* = 4 */,
    Z_BLOCK /* = 5 */,
    Z_TREES /* = 6 */
}

external enum class StrategyValues {
    Z_FILTERED /* = 1 */,
    Z_HUFFMAN_ONLY /* = 2 */,
    Z_RLE /* = 3 */,
    Z_FIXED /* = 4 */,
    Z_DEFAULT_STRATEGY /* = 0 */
}

external enum class ReturnCodes {
    Z_OK /* = 0 */,
    Z_STREAM_END /* = 1 */,
    Z_NEED_DICT /* = 2 */,
    Z_ERRNO /* = -1 */,
    Z_STREAM_ERROR /* = -2 */,
    Z_DATA_ERROR /* = -3 */,
    Z_BUF_ERROR /* = -5 */
}

external interface DeflateOptions {
    var level: dynamic /* "-1" | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 */
        get() = definedExternally
        set(value) = definedExternally
    var windowBits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var memLevel: Number?
        get() = definedExternally
        set(value) = definedExternally
    var strategy: StrategyValues?
        get() = definedExternally
        set(value) = definedExternally
    var dictionary: Any?
        get() = definedExternally
        set(value) = definedExternally
    var raw: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var to: String? /* "string" */
        get() = definedExternally
        set(value) = definedExternally
    var chunkSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gzip: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var header: Header?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeflateFunctionOptions {
    var level: dynamic /* "-1" | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 */
        get() = definedExternally
        set(value) = definedExternally
    var windowBits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var memLevel: Number?
        get() = definedExternally
        set(value) = definedExternally
    var strategy: StrategyValues?
        get() = definedExternally
        set(value) = definedExternally
    var dictionary: Any?
        get() = definedExternally
        set(value) = definedExternally
    var raw: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var to: String? /* "string" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface InflateOptions {
    var windowBits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dictionary: Any?
        get() = definedExternally
        set(value) = definedExternally
    var raw: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var to: String? /* "string" */
        get() = definedExternally
        set(value) = definedExternally
    var chunkSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface InflateFunctionOptions {
    var windowBits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var raw: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var to: String? /* "string" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface Header {
    var text: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var time: Number?
        get() = definedExternally
        set(value) = definedExternally
    var os: Number?
        get() = definedExternally
        set(value) = definedExternally
    var extra: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var comment: String?
        get() = definedExternally
        set(value) = definedExternally
    var hcrc: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var to: String /* "string" */
}

external fun deflate(data: Uint8Array, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflate(data: Array<Number>, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflate(data: String, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflate(data: Uint8Array): Uint8Array

external fun deflate(data: Array<Number>): Uint8Array

external fun deflate(data: String): Uint8Array

external fun deflateRaw(data: Uint8Array, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflateRaw(data: Array<Number>, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflateRaw(data: String, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun deflateRaw(data: Uint8Array): Uint8Array

external fun deflateRaw(data: Array<Number>): Uint8Array

external fun deflateRaw(data: String): Uint8Array

external fun gzip(data: Uint8Array, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun gzip(data: Array<Number>, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun gzip(data: String, options: DeflateFunctionOptions /* DeflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun gzip(data: Uint8Array): Uint8Array

external fun gzip(data: Array<Number>): Uint8Array

external fun gzip(data: String): Uint8Array

external fun inflate(data: Uint8Array, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflate(data: Array<Number>, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflate(data: String, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflate(data: Uint8Array): Uint8Array

external fun inflate(data: Array<Number>): Uint8Array

external fun inflate(data: String): Uint8Array

external fun inflateRaw(data: Uint8Array, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflateRaw(data: Array<Number>, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflateRaw(data: String, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun inflateRaw(data: Uint8Array): Uint8Array

external fun inflateRaw(data: Array<Number>): Uint8Array

external fun inflateRaw(data: String): Uint8Array

external fun ungzip(data: Uint8Array, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun ungzip(data: Array<Number>, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun ungzip(data: String, options: InflateFunctionOptions /* InflateFunctionOptions & `T$0` */): dynamic /* String | Uint8Array */

external fun ungzip(data: Uint8Array): Uint8Array

external fun ungzip(data: Array<Number>): Uint8Array

external fun ungzip(data: String): Uint8Array

external open class Deflate(options: DeflateOptions = definedExternally) {
    open var err: ReturnCodes
    open var msg: String
    open var result: dynamic /* Uint8Array | Array<Number> */
    open fun onData(chunk: Uint8Array)
    open fun onData(chunk: Array<Number>)
    open fun onData(chunk: String)
    open fun onEnd(status: Number)
    open fun push(data: Uint8Array, mode: FlushValues = definedExternally): Boolean
    open fun push(data: Uint8Array): Boolean
    open fun push(data: Uint8Array, mode: Boolean = definedExternally): Boolean
    open fun push(data: Array<Number>, mode: FlushValues = definedExternally): Boolean
    open fun push(data: Array<Number>): Boolean
    open fun push(data: Array<Number>, mode: Boolean = definedExternally): Boolean
    open fun push(data: String, mode: FlushValues = definedExternally): Boolean
    open fun push(data: String): Boolean
    open fun push(data: String, mode: Boolean = definedExternally): Boolean
    open fun push(data: ArrayBuffer, mode: FlushValues = definedExternally): Boolean
    open fun push(data: ArrayBuffer): Boolean
    open fun push(data: ArrayBuffer, mode: Boolean = definedExternally): Boolean
}

external open class Inflate(options: InflateOptions = definedExternally) {
    open var header: Header
    open var err: ReturnCodes
    open var msg: String
    open var result: dynamic /* Uint8Array | Array<Number> | String */
    open fun onData(chunk: Uint8Array)
    //open fun onData(chunk: Array<Number>)
    //open fun onData(chunk: String)
    open fun onEnd(status: Number)
    open fun push(data: Uint8Array, mode: FlushValues = definedExternally): Boolean
    open fun push(data: Uint8Array): Boolean
    open fun push(data: Uint8Array, mode: Boolean = definedExternally): Boolean
    open fun push(data: Array<Number>, mode: FlushValues = definedExternally): Boolean
    open fun push(data: Array<Number>): Boolean
    open fun push(data: Array<Number>, mode: Boolean = definedExternally): Boolean
    open fun push(data: String, mode: FlushValues = definedExternally): Boolean
    open fun push(data: String): Boolean
    open fun push(data: String, mode: Boolean = definedExternally): Boolean
    open fun push(data: ArrayBuffer, mode: FlushValues = definedExternally): Boolean
    open fun push(data: ArrayBuffer): Boolean
    open fun push(data: ArrayBuffer, mode: Boolean = definedExternally): Boolean
}