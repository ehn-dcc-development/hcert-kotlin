@file:JsModule("pvutils")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package PvUtils

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import kotlin.js.Date

external fun getUTCDate(date: Date): Date

external fun <T> getParametersValue(parameters: Any, name: String, defaultValue: T = definedExternally): T

external fun bufferToHexCodes(
    inputBuffer: ArrayBuffer,
    inputOffset: Number = definedExternally,
    inputLength: Number = definedExternally
): String

external fun checkBufferParams(
    baseBlock: Any,
    inputBuffer: ArrayBuffer,
    inputOffset: Number,
    inputLength: Number
): Boolean

external fun utilFromBase(inputBuffer: Uint8Array, inputBase: Number): Number

external fun utilToBase(value: Number, base: Number, reserved: Number = definedExternally): ArrayBuffer

external fun utilConcatBuf(vararg buf: ArrayBuffer): ArrayBuffer

external fun utilDecodeTC(): Number

external fun utilEncodeTC(value: Number): ArrayBuffer

external fun isEqualBuffer(inputBuffer1: ArrayBuffer, inputBuffer2: ArrayBuffer): Boolean

external fun padNumber(inputNumber: Number, fullLength: Number): String

external fun toBase64(
    input: String,
    useUrlTemplate: Boolean = definedExternally,
    skipPadding: Boolean = definedExternally
): String

external fun fromBase64(
    input: String,
    useUrlTemplate: Boolean = definedExternally,
    cutTailZeros: Boolean = definedExternally
): String

external fun arrayBufferToString(buffer: ArrayBuffer): String

external fun arrayBufferToString(buffer: ArrayBufferView): String

external fun stringToArrayBuffer(str: String): ArrayBuffer

external fun nearestPowerOf2(length: Number): Number