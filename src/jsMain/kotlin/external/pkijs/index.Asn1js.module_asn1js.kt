@file:JsModule("asn1js")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package Asn1js

import org.khronos.webgl.ArrayBuffer
import kotlin.js.Date

external interface LocalBaseBlockParams {
    var blockLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var error: String?
        get() = definedExternally
        set(value) = definedExternally
    var warnings: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var valueBeforeDecode: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
}

external interface JsonLocalBaseBlock {
    var blockName: String
    var blockLength: Number
    var error: String
    var warnings: Array<String>
    var valueBeforeDecode: ArrayBuffer
}

open external class LocalBaseBlock(params: LocalBaseBlockParams = definedExternally) {
    open var blockLength: Number
    open var error: String
    open var warnings: Array<String>
    open var valueBeforeDecode: ArrayBuffer
    open fun toJSON(): Any

    companion object {
        fun blockName(): String
    }
}

external interface LocalHexBlockParams : LocalBaseBlockParams {
    var isHexOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var valueHex: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ILocalHexBlock {
    var isHexOnly: Boolean
    var valueHex: ArrayBuffer
    fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
    fun toJSON(): Any
}

open external class LocalHexBlock(params: Any) : ILocalHexBlock {
    override var isHexOnly: Boolean
    override var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
    override fun toJSON(): Any

    companion object {
        fun blockName(): String
    }
}

external interface `T$10` {
    var isConstructed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var tagClass: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tagNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface LocalIdentificationBlockParams {
    var idBlock: LocalHexBlockParams? /* LocalHexBlockParams? & `T$10`? */
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalIdentificationBlock(params: LocalIdentificationBlockParams = definedExternally) :
    LocalBaseBlock, ILocalHexBlock {
    open var isConstructed: Boolean
    open var tagClass: Number
    open var tagNumber: Number
    override var isHexOnly: Boolean
    override var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

external interface `T$11` {
    var isIndefiniteForm: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var longFormUsed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var length: Number
}

external interface LocalLengthBlockParams {
    var lenBlock: `T$11`?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalLengthBlock(params: LocalLengthBlockParams = definedExternally) : LocalBaseBlock {
    open var isIndefiniteForm: Boolean
    open var longFormUsed: Boolean
    open var length: Number
    open fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number)
    open fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
}

open external class LocalValueBlock(params: LocalBaseBlockParams = definedExternally) : LocalBaseBlock {
    open fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number)
    open fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
}

external interface BaseBlockParams : LocalBaseBlockParams {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var optional: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var primitiveSchema: Any?
        get() = definedExternally
        set(value) = definedExternally
}

open external class BaseBlock<T : LocalValueBlock>(
    parameters: BaseBlockParams = definedExternally,
    valueBlockType: Any = definedExternally
) : LocalBaseBlock {
    open var idBlock: LocalIdentificationBlock
    open var lenBlock: LocalLengthBlock
    open var valueBlock: T
    open fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    open fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
}

external interface LocalPrimitiveValueBlockParams : LocalBaseBlockParams {
    var valueHex: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var isHexOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalPrimitiveValueBlock(params: LocalBaseBlockParams = definedExternally) : LocalValueBlock {
    open var valueHex: ArrayBuffer
    open var isHexOnly: Boolean
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

open external class Primitive(
    parameters: BaseBlockParams = definedExternally,
    valueBlockType: Any = definedExternally
) : BaseBlock<LocalPrimitiveValueBlock>

external interface LocalConstructedValueBlockParams : LocalBaseBlockParams {
    var value: LocalValueBlock?
        get() = definedExternally
        set(value) = definedExternally
    var isIndefiniteForm: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalConstructedValueBlock(params: LocalBaseBlockParams = definedExternally) : LocalValueBlock {
    open var value: Array<LocalValueBlock>
    open var isIndefiniteForm: Boolean
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

open external class Constructed(
    parameters: BaseBlockParams = definedExternally,
    valueBlockType: Any = definedExternally
) : BaseBlock<LocalConstructedValueBlock>

external interface LocalEndOfContentValueBlockParams : LocalBaseBlockParams

open external class LocalEndOfContentValueBlock(params: LocalEndOfContentValueBlockParams = definedExternally) :
    LocalValueBlock {
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

open external class EndOfContent(
    parameters: BaseBlockParams = definedExternally,
    valueBlockType: Any = definedExternally
) : BaseBlock<LocalEndOfContentValueBlock>

external interface LocalBooleanValueBlockParams : LocalBaseBlockParams {
    var value: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isHexOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var valueHex: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalBooleanValueBlock(params: LocalBaseBlockParams = definedExternally) : LocalValueBlock {
    open var value: Boolean
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

open external class Boolean(parameters: BaseBlockParams = definedExternally, valueBlockType: Any = definedExternally) :
    BaseBlock<LocalBooleanValueBlock>

open external class Sequence(parameters: BaseBlockParams = definedExternally, valueBlockType: Any = definedExternally) :
    Constructed

open external class Set(parameters: BaseBlockParams = definedExternally, valueBlockType: Any = definedExternally) :
    Constructed

open external class Null(parameters: BaseBlockParams = definedExternally, valueBlockType: Any = definedExternally) :
    BaseBlock<LocalValueBlock>

external interface LocalOctetStringValueBlockParams : LocalConstructedValueBlockParams, LocalHexBlockParams {
    var isConstructed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalOctetStringValueBlock(params: LocalOctetStringValueBlockParams = definedExternally) :
    LocalConstructedValueBlock, ILocalHexBlock {
    open var isConstructed: Boolean
    override var isHexOnly: Boolean
    override var valueHex: ArrayBuffer
}

open external class OctetString(params: LocalOctetStringValueBlockParams = definedExternally) :
    BaseBlock<LocalOctetStringValueBlock> {
    open fun isEqual(octetString: OctetString): Boolean
}

external interface LocalBitStringValueBlockParams : LocalConstructedValueBlockParams, LocalHexBlockParams {
    var unusedBits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var isConstructed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    override var blockLength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalBitStringValueBlock(params: LocalBitStringValueBlockParams = definedExternally) :
    LocalConstructedValueBlock {
    open var unusedBits: Number
    open var isConstructed: Boolean
    override var blockLength: Number
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
}

open external class BitString(params: LocalBitStringValueBlockParams = definedExternally) :
    BaseBlock<LocalBitStringValueBlock> {
    open fun isEqual(bitString: BitString): Boolean
}

external interface LocalIntegerValueBlockParams : LocalBaseBlockParams, LocalHexBlockParams

open external class LocalIntegerValueBlock(params: LocalIntegerValueBlockParams = definedExternally) : LocalValueBlock {
    open var valueDec: Number
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
    open fun fromDER(
        inputBuffer: ArrayBuffer,
        inputOffset: Number,
        inputLength: Number,
        expectedLength: Number = definedExternally
    ): Number

    open fun toDER(sizeOnly: Boolean = definedExternally): ArrayBuffer
}

external interface IntegerParams {
    var value: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Integer(params: IntegerParams = definedExternally) : BaseBlock<LocalIntegerValueBlock> {
    open fun isEqual(otherValue: Integer): Boolean
    open fun isEqual(otherValue: ArrayBuffer): Boolean
    open fun convertToDER(): Integer
    open fun convertFromDER(): Integer
}

open external class Enumerated(params: IntegerParams = definedExternally) : Integer

external interface LocalSidValueBlockParams : LocalBaseBlockParams, LocalHexBlockParams {
    var valueDec: Number?
        get() = definedExternally
        set(value) = definedExternally
    var isFirstSid: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalSidValueBlock(params: LocalSidValueBlockParams = definedExternally) : LocalBaseBlock {
    open var valueDec: Number
    open var isFirstSid: Boolean
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    open fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    open fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
    override fun toString(): String
}

external interface LocalObjectIdentifierValueBlockParams : LocalBaseBlockParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalObjectIdentifierValueBlock(params: LocalObjectIdentifierValueBlockParams = definedExternally) :
    LocalValueBlock {
    open fun fromString(data: String): Boolean
    override fun toString(): String
}

open external class ObjectIdentifier(params: LocalObjectIdentifierValueBlockParams = definedExternally) :
    BaseBlock<LocalObjectIdentifierValueBlock>

external interface LocalUtf8StringValueBlockParams : LocalBaseBlockParams, LocalHexBlock

open external class LocalUtf8StringValueBlock(params: LocalSidValueBlockParams = definedExternally) : LocalValueBlock {
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
    override fun toString(): String
}

external interface Utf8StringParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Utf8String(params: Utf8StringParams = definedExternally) : BaseBlock<LocalUtf8StringValueBlock> {
    open fun fromBuffer(inputBuffer: ArrayBuffer)
    open fun fromString(inputString: String)
}

external interface LocalBmpStringValueBlockParams : LocalHexBlockParams, LocalBaseBlockParams

open external class LocalBmpStringValueBlock(params: LocalBmpStringValueBlockParams = definedExternally) :
    LocalValueBlock {
    open var value: String
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
}

external interface BmpStringParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class BmpString(params: BmpStringParams = definedExternally) : BaseBlock<LocalBmpStringValueBlock> {
    open fun fromBuffer(inputBuffer: ArrayBuffer)
    open fun fromString(inputString: String)
}

external interface LocalUniversalStringValueParams : LocalHexBlockParams, LocalBaseBlockParams

open external class LocalUniversalStringValueBlock(params: LocalUniversalStringValueParams = definedExternally) :
    LocalValueBlock {
    open var value: String
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

external interface UniversalStringParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class UniversalString(params: UniversalStringParams = definedExternally) :
    BaseBlock<LocalUniversalStringValueBlock> {
    open fun fromBuffer(inputBuffer: ArrayBuffer)
    open fun fromString(inputString: String)
}

external interface LocalSimpleLocalSimpleStringValueBlockParams : LocalHexBlockParams, LocalBaseBlockParams

open external class LocalSimpleLocalSimpleStringValueBlock(params: LocalSimpleLocalSimpleStringValueBlockParams = definedExternally) :
    LocalValueBlock {
    open var value: String
    open var isHexOnly: Boolean
    open var valueHex: ArrayBuffer
    override fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    override fun toBER(sizeOnly: Boolean): ArrayBuffer
}

external interface LocalSimpleStringBlockParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LocalSimpleStringBlock(params: LocalSimpleStringBlockParams = definedExternally) :
    BaseBlock<LocalSimpleLocalSimpleStringValueBlock> {
    open fun fromBuffer(inputBuffer: ArrayBuffer)
    open fun fromString(inputString: String)
}

open external class NumericString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class PrintableString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class TeletexString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class VideotexString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class IA5String(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class GraphicString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class VisibleString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class GeneralString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

open external class CharacterString(params: LocalSimpleStringBlockParams = definedExternally) : LocalSimpleStringBlock

external interface UTCTimeParams : LocalSimpleLocalSimpleStringValueBlockParams {
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
    var valueDate: Date?
        get() = definedExternally
        set(value) = definedExternally
}

open external class UTCTime(params: UTCTimeParams = definedExternally) : VisibleString {
    open var year: Number
    open var month: Number
    open var day: Number
    open var hour: Number
    open var minute: Number
    open var second: Number
    open fun toBuffer(): ArrayBuffer
    open fun fromDate(inputDate: Date)
    open fun toDate(): Date
}

open external class GeneralizedTime(params: UTCTimeParams = definedExternally) : UTCTime {
    open var millisecond: Number
}

open external class DATE(params: Utf8StringParams = definedExternally) : Utf8String

open external class TimeOfDay(params: Utf8StringParams = definedExternally) : Utf8String

open external class DateTime(params: Utf8StringParams = definedExternally) : Utf8String

open external class Duration(params: Utf8StringParams = definedExternally) : Utf8String

open external class TIME(params: Utf8StringParams = definedExternally) : Utf8String

external interface ChoiceParams {
    var value: Array<LocalValueBlock>?
        get() = definedExternally
        set(value) = definedExternally
    var optional: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Choice(params: ChoiceParams = definedExternally) {
    open var value: Array<LocalValueBlock>
    open var optional: Boolean
}

external interface AnyParams {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var optional: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Any(params: AnyParams = definedExternally) {
    open var name: String
    open var optional: Boolean
}

external interface RepeatedParams {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var optional: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: Any?
        get() = definedExternally
        set(value) = definedExternally
    var local: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Repeated(params: RepeatedParams = definedExternally) {
    open var name: String
    open var optional: Boolean
    open var value: Any
    open var local: Boolean
}

external interface RawDataParams {
    var data: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
}

open external class RawData(params: RawDataParams = definedExternally) {
    open var data: ArrayBuffer
    open fun fromBER(inputBuffer: ArrayBuffer, inputOffset: Number, inputLength: Number): Number
    open fun toBER(sizeOnly: Boolean = definedExternally): ArrayBuffer
}

external interface `T$12` {
    var offset: Number
    var result: LocalBaseBlock
}

external fun fromBER(inputBuffer: ArrayBuffer): `T$12`

external interface `T$13` {
    var verified: Boolean
    var result: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external fun compareSchema(root: Any, inputData: Any, inputSchema: Any): `T$13`

external fun verifySchema(inputBuffer: ArrayBuffer, inputSchema: Any): `T$13`