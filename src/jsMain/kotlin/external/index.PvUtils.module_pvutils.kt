@file:JsModule("pvutils")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package PvUtils

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external fun getUTCDate(date: Date): Date

external fun <T> getParametersValue(parameters: Any, name: String, defaultValue: T = definedExternally): T

external fun bufferToHexCodes(inputBuffer: ArrayBuffer, inputOffset: Number = definedExternally, inputLength: Number = definedExternally): String

external fun checkBufferParams(baseBlock: Any, inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Boolean

external fun utilFromBase(inputBuffer: Uint8Array, inputBase: Number): Number

external fun utilToBase(value: Number, base: Number, reserved: Number = definedExternally): ArrayBuffer

external fun utilConcatBuf(vararg buf: ArrayBuffer): ArrayBuffer

external fun utilDecodeTC(): Number

external fun utilEncodeTC(value: Number): ArrayBuffer

external fun isEqualBuffer(inputBuffer1: ArrayBuffer, inputBuffer2: ArrayBuffer): Boolean

external fun padNumber(inputNumber: Number, fullLength: Number): String

external fun toBase64(input: String, useUrlTemplate: Boolean = definedExternally, skipPadding: Boolean = definedExternally): String

external fun fromBase64(input: String, useUrlTemplate: Boolean = definedExternally, cutTailZeros: Boolean = definedExternally): String

external fun arrayBufferToString(buffer: ArrayBuffer): String

external fun arrayBufferToString(buffer: ArrayBufferView): String

external fun stringToArrayBuffer(str: String): ArrayBuffer

external fun nearestPowerOf2(length: Number): Number