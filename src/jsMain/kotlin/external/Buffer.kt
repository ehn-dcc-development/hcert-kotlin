@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")


import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

external interface `T$0` {
    var type: String /* "Buffer" */
    var data: Array<Any>
}

external open class Buffer : Uint8Array {
    constructor(str: String, encoding: String = definedExternally)
    constructor(str: String)
    constructor(size: Number)
    constructor(array: Uint8Array)
    constructor(arrayBuffer: ArrayBuffer)
    constructor(array: Array<Any>)
    constructor(buffer: Buffer)
    override var length: Int
    open fun write(string: String, offset: Number = definedExternally, length: Number = definedExternally, encoding: String = definedExternally): Number
    open fun toString(encoding: String = definedExternally, start: Number = definedExternally, end: Number = definedExternally): String
    open fun toJSON(): `T$0`
    open fun equals(otherBuffer: Buffer): Boolean
    open fun compare(otherBuffer: Uint8Array, targetStart: Number = definedExternally, targetEnd: Number = definedExternally, sourceStart: Number = definedExternally, sourceEnd: Number = definedExternally): Number
    open fun copy(targetBuffer: Buffer, targetStart: Number = definedExternally, sourceStart: Number = definedExternally, sourceEnd: Number = definedExternally): Number
    open fun slice(start: Number = definedExternally, end: Number = definedExternally): Buffer
    open fun writeUIntLE(value: Number, offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun writeUIntBE(value: Number, offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun writeIntLE(value: Number, offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun writeIntBE(value: Number, offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun readUIntLE(offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun readUIntBE(offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun readIntLE(offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun readIntBE(offset: Number, byteLength: Number, noAssert: Boolean = definedExternally): Number
    open fun readUInt8(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readUInt16LE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readUInt16BE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readUInt32LE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readUInt32BE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readInt8(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readInt16LE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readInt16BE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readInt32LE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readInt32BE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readFloatLE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readFloatBE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readDoubleLE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun readDoubleBE(offset: Number, noAssert: Boolean = definedExternally): Number
    open fun reverse(): Buffer /* this */
    open fun swap16(): Buffer
    open fun swap32(): Buffer
    open fun swap64(): Buffer
    open fun writeUInt8(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeUInt16LE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeUInt16BE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeUInt32LE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeUInt32BE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeInt8(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeInt16LE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeInt16BE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeInt32LE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeInt32BE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeFloatLE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeFloatBE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeDoubleLE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun writeDoubleBE(value: Number, offset: Number, noAssert: Boolean = definedExternally): Number
    open fun fill(value: Any, offset: Number = definedExternally, end: Number = definedExternally): Buffer /* this */
    open fun indexOf(value: String, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun indexOf(value: String): Number
    open fun indexOf(value: String, byteOffset: Number = definedExternally): Number
    open fun indexOf(value: Number, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun indexOf(value: Number): Number
    open fun indexOf(value: Number, byteOffset: Number = definedExternally): Number
    open fun indexOf(value: Buffer, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun indexOf(value: Buffer): Number
    open fun indexOf(value: Buffer, byteOffset: Number = definedExternally): Number
    open fun lastIndexOf(value: String, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun lastIndexOf(value: String): Number
    open fun lastIndexOf(value: String, byteOffset: Number = definedExternally): Number
    open fun lastIndexOf(value: Number, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun lastIndexOf(value: Number): Number
    open fun lastIndexOf(value: Number, byteOffset: Number = definedExternally): Number
    open fun lastIndexOf(value: Buffer, byteOffset: Number = definedExternally, encoding: String = definedExternally): Number
    open fun lastIndexOf(value: Buffer): Number
    open fun lastIndexOf(value: Buffer, byteOffset: Number = definedExternally): Number
    open fun includes(value: String, byteOffset: Number = definedExternally, encoding: String = definedExternally): Boolean
    open fun includes(value: String): Boolean
    open fun includes(value: String, byteOffset: Number = definedExternally): Boolean
    open fun includes(value: Number, byteOffset: Number = definedExternally, encoding: String = definedExternally): Boolean
    open fun includes(value: Number): Boolean
    open fun includes(value: Number, byteOffset: Number = definedExternally): Boolean
    open fun includes(value: Buffer, byteOffset: Number = definedExternally, encoding: String = definedExternally): Boolean
    open fun includes(value: Buffer): Boolean
    open fun includes(value: Buffer, byteOffset: Number = definedExternally): Boolean
    open var prototype: Buffer

    companion object {
        fun from(array: Array<Any>): Buffer
        fun from(arrayBuffer: ArrayBuffer, byteOffset: Number = definedExternally, length: Number = definedExternally): Buffer
        fun from(buffer: Buffer): Buffer
        fun from(buffer: Uint8Array): Buffer
        fun from(str: String, encoding: String = definedExternally): Buffer
        fun isBuffer(obj: Any): Boolean
        fun isEncoding(encoding: String): Boolean
        fun byteLength(string: String, encoding: String = definedExternally): Number
        fun concat(list: Array<Uint8Array>, totalLength: Number = definedExternally): Buffer
        fun compare(buf1: Uint8Array, buf2: Uint8Array): Number
        fun alloc(size: Number, fill: String = definedExternally, encoding: String = definedExternally): Buffer
        fun alloc(size: Number, fill: Buffer = definedExternally, encoding: String = definedExternally): Buffer
        fun alloc(size: Number, fill: Number = definedExternally, encoding: String = definedExternally): Buffer
        fun allocUnsafe(size: Number): Buffer
        fun allocUnsafeSlow(size: Number): Buffer
    }
}