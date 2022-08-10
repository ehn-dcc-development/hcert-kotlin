@file:JsModule("@nuintun/qrcode")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

package qrcode

import org.khronos.webgl.Uint8ClampedArray
import kotlin.js.Promise

external enum class ErrorCorrectionLevel {
    L /* = 1 */,
    M /* = 0 */,
    Q /* = 3 */,
    H /* = 2 */
}

external open class Encoder {
    open var version: Any
    open var chunks: Any
    open var matrixSize: Any
    open var matrix: Any
    open var encodingHint: Any
    open var auto: Any
    open var errorCorrectionLevel: Any
    open fun getMatrix(): Array<Array<Boolean>>
    open fun getMatrixSize(): Number
    open fun getVersion(): Number
    open fun setVersion(version: Number): Encoder
    open fun getErrorCorrectionLevel(): ErrorCorrectionLevel
    open fun setErrorCorrectionLevel(errorCorrectionLevel: ErrorCorrectionLevel): Encoder
    open fun getEncodingHint(): Boolean
    open fun setEncodingHint(encodingHint: Boolean): Encoder
    open fun write(data: QRData): Encoder
    open fun write(data: String): Encoder
    open fun isDark(row: Number, col: Number): Boolean
    open var setupFinderPattern: Any
    open var setupAlignmentPattern: Any
    open var setupTimingPattern: Any
    open var setupFormatInfo: Any
    open var setupVersionInfo: Any
    open var setupCodewords: Any
    open var buildMatrix: Any
    open fun make(): Encoder
    open fun toDataURL(moduleSize: Number = definedExternally, margin: Number = definedExternally): String
}

external interface `T$012` {
    var topLeft: Point
    var topRight: Point
    var bottomLeft: Point
    var bottomRight: Point
    var topLeftFinder: Point
    var topRightFinder: Point
    var bottomLeftFinder: Point
    var bottomRightAlignment: Point?
}

external interface DecodeData {
    var data: String
    var bytes: Array<Number>
}

external interface DecodeResult : DecodeData {
    var chunks: Array<dynamic /* ByteChunk | StructuredAppendChunk */>
    var version: Number
    var errorCorrectionLevel: ErrorCorrectionLevel
}

external interface DecoderResult : DecodeResult {
    var location: `T$012`
}

external interface Options {
    var canOverwriteImage: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var greyScaleWeights: GreyscaleWeights?
        get() = definedExternally
        set(value) = definedExternally
    var inversionAttempts: String? /* "dontInvert" | "onlyInvert" | "attemptBoth" | "invertFirst" */
        get() = definedExternally
        set(value) = definedExternally
}

external open class Decoder {
    open var options: Any
    open fun setOptions(options: Options = definedExternally): Decoder
    open fun decode(data: Uint8ClampedArray, width: Number, height: Number): DecoderResult
    open fun scan(src: String): Promise<DecoderResult>
}

external open class QRData(mode: Mode, data: String) {
    open var mode: Mode
    open var data: String
    open var bytes: Array<Number>
    open fun getMode(): Mode
    open fun getLength(): Number
    open fun write(buffer: BitBuffer)
    open fun getLengthInBits(version: Number): Number
}

external open class QRAlphanumeric(data: String) : QRData {
    override fun getLength(): Number
    override fun write(buffer: BitBuffer)
}

external open class BitBuffer {
    open var length: Any
    open var buffer: Any
    open fun getBuffer(): Array<Number>
    open fun getLengthInBits(): Number
    open fun getBit(index: Number): Boolean
    open fun put(num: Number, length: Number)
    open fun putBit(bit: Boolean)
}

external enum class Mode {
    Terminator /* = 0 */,
    Numeric /* = 1 */,
    Alphanumeric /* = 2 */,
    StructuredAppend /* = 3 */,
    Byte /* = 4 */,
    Kanji /* = 8 */,
    ECI /* = 7 */
}

external interface Point {
    var x: Number
    var y: Number
}

external interface GreyscaleWeights {
    var red: Number
    var green: Number
    var blue: Number
    var useIntegerApproximation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}