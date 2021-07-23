@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

import org.khronos.webgl.Uint8Array

external interface `T$2` {
    var a: BN
    var b: BN
    var gcd: BN
}

@JsModule("bn.js")
open external class BN {
    constructor(number: Number, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Number)
    constructor(number: Number, base: Number = definedExternally)
    constructor(number: Number, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Number, base: String = definedExternally)
    constructor(number: String, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: String)
    constructor(number: String, base: Number = definedExternally)
    constructor(number: String, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: String, base: String = definedExternally)
    constructor(number: Array<Number>, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Array<Number>)
    constructor(number: Array<Number>, base: Number = definedExternally)
    constructor(number: Array<Number>, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Array<Number>, base: String = definedExternally)
    constructor(number: Uint8Array, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Uint8Array)
    constructor(number: Uint8Array, base: Number = definedExternally)
    constructor(number: Uint8Array, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Uint8Array, base: String = definedExternally)
    constructor(number: Buffer, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Buffer)
    constructor(number: Buffer, base: Number = definedExternally)
    constructor(number: Buffer, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Buffer, base: String = definedExternally)
    constructor(number: BN, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: BN)
    constructor(number: BN, base: Number = definedExternally)
    constructor(number: BN, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: BN, base: String = definedExternally)
    constructor(number: Number, endian: String = definedExternally)
    constructor(number: String, endian: String = definedExternally)
    constructor(number: Array<Number>, endian: String = definedExternally)
    constructor(number: Uint8Array, endian: String = definedExternally)
    constructor(number: Buffer, endian: String = definedExternally)
    constructor(number: BN, endian: String = definedExternally)

    open fun clone(): BN
    open fun toString(base: Number = definedExternally, length: Number = definedExternally): String
    override fun toString(): String
    open fun toString(base: Number = definedExternally): String
    open fun toString(base: String /* "hex" */ = definedExternally, length: Number = definedExternally): String
    open fun toString(base: String /* "hex" */ = definedExternally): String
    open fun toNumber(): Number
    open fun toJSON(): String
    open fun toArray(
        endian: String /* "le" | "be" */ = definedExternally,
        length: Number = definedExternally
    ): Array<Number>

    open fun toArrayLike(
        ArrayType: Any,
        endian: String /* "le" | "be" */ = definedExternally,
        length: Number = definedExternally
    ): Buffer

    open fun toArrayLike(ArrayType: Any): Buffer
    open fun toArrayLike(ArrayType: Any, endian: String /* "le" | "be" */ = definedExternally): Buffer
    open fun toArrayLike(
        ArrayType: Array<Any>,
        endian: String /* "le" | "be" */ = definedExternally,
        length: Number = definedExternally
    ): Array<Any>

    open fun toArrayLike(ArrayType: Array<Any>): Array<Any>
    open fun toArrayLike(ArrayType: Array<Any>, endian: String /* "le" | "be" */ = definedExternally): Array<Any>
    open fun toBuffer(endian: String /* "le" | "be" */ = definedExternally, length: Number = definedExternally): Buffer
    open fun bitLength(): Number
    open fun zeroBits(): Number
    open fun byteLength(): Number
    open fun isNeg(): Boolean
    open fun isEven(): Boolean
    open fun isOdd(): Boolean
    open fun isZero(): Boolean
    open fun cmp(b: BN): dynamic /* "-1" | 0 | 1 */
    open fun ucmp(b: BN): dynamic /* "-1" | 0 | 1 */
    open fun cmpn(b: Number): dynamic /* "-1" | 0 | 1 */
    open fun lt(b: BN): Boolean
    open fun ltn(b: Number): Boolean
    open fun lte(b: BN): Boolean
    open fun lten(b: Number): Boolean
    open fun gt(b: BN): Boolean
    open fun gtn(b: Number): Boolean
    open fun gte(b: BN): Boolean
    open fun gten(b: Number): Boolean
    open fun eq(b: BN): Boolean
    open fun eqn(b: Number): Boolean
    open fun toTwos(width: Number): BN
    open fun fromTwos(width: Number): BN
    open fun neg(): BN
    open fun ineg(): BN
    open fun abs(): BN
    open fun iabs(): BN
    open fun add(b: BN): BN
    open fun iadd(b: BN): BN
    open fun addn(b: Number): BN
    open fun iaddn(b: Number): BN
    open fun sub(b: BN): BN
    open fun isub(b: BN): BN
    open fun subn(b: Number): BN
    open fun isubn(b: Number): BN
    open fun mul(b: BN): BN
    open fun imul(b: BN): BN
    open fun muln(b: Number): BN
    open fun imuln(b: Number): BN
    open fun sqr(): BN
    open fun isqr(): BN
    open fun pow(b: BN): BN
    open fun div(b: BN): BN
    open fun divn(b: Number): BN
    open fun idivn(b: Number): BN
    open fun mod(b: BN): BN
    open fun umod(b: BN): BN
    open fun modn(b: Number): Number
    open fun modrn(b: Number): Number
    open fun divRound(b: BN): BN
    open fun or(b: BN): BN
    open fun ior(b: BN): BN
    open fun uor(b: BN): BN
    open fun iuor(b: BN): BN
    open fun and(b: BN): BN
    open fun iand(b: BN): BN
    open fun uand(b: BN): BN
    open fun iuand(b: BN): BN
    open fun andln(b: Number): BN
    open fun xor(b: BN): BN
    open fun ixor(b: BN): BN
    open fun uxor(b: BN): BN
    open fun iuxor(b: BN): BN
    open fun setn(b: Number): BN
    open fun shln(b: Number): BN
    open fun ishln(b: Number): BN
    open fun ushln(b: Number): BN
    open fun iushln(b: Number): BN
    open fun shrn(b: Number): BN
    open fun ishrn(b: Number): BN
    open fun ushrn(b: Number): BN
    open fun iushrn(b: Number): BN
    open fun testn(b: Number): Boolean
    open fun maskn(b: Number): BN
    open fun imaskn(b: Number): BN
    open fun bincn(b: Number): BN
    open fun notn(w: Number): BN
    open fun inotn(w: Number): BN
    open fun gcd(b: BN): BN
    open fun egcd(b: BN): `T$2`
    open fun invm(b: BN): BN
    open fun toRed(reductionContext: ReductionContext): RedBN
    interface MPrime {
        var name: String
        var p: BN
        var n: Number
        var k: BN
    }
    interface ReductionContext {
        var m: Number
        var prime: MPrime
        @nativeGetter
        operator fun get(key: String): Any?
        @nativeSetter
        operator fun set(key: String, value: Any)
    }

    companion object {
        var BN: Any
        var wordSize: Number /* 26 */
        fun red(reductionContext: BN): ReductionContext
        fun red(reductionContext: String /* "k256" */): ReductionContext
        fun mont(num: BN): ReductionContext
        fun isBN(b: Any): Boolean
        fun max(left: BN, right: BN): BN
        fun min(left: BN, right: BN): BN
    }
}

@Suppress("NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
inline operator fun BN.ReductionContext.get(key: String): Any? = asDynamic()[key]

@Suppress("NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
inline operator fun BN.ReductionContext.set(key: String, value: Any) {
    asDynamic()[key] = value
}

open external class RedBN : BN {
    constructor(number: Number, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Number)
    constructor(number: Number, base: Number = definedExternally)
    constructor(number: Number, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Number, base: String = definedExternally)
    constructor(number: String, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: String)
    constructor(number: String, base: Number = definedExternally)
    constructor(number: String, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: String, base: String = definedExternally)
    constructor(number: Array<Number>, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Array<Number>)
    constructor(number: Array<Number>, base: Number = definedExternally)
    constructor(number: Array<Number>, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Array<Number>, base: String = definedExternally)
    constructor(number: Uint8Array, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Uint8Array)
    constructor(number: Uint8Array, base: Number = definedExternally)
    constructor(number: Uint8Array, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Uint8Array, base: String = definedExternally)
    constructor(number: Buffer, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: Buffer)
    constructor(number: Buffer, base: Number = definedExternally)
    constructor(number: Buffer, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: Buffer, base: String = definedExternally)
    constructor(number: BN, base: Number = definedExternally, endian: String = definedExternally)
    constructor(number: BN)
    constructor(number: BN, base: Number = definedExternally)
    constructor(number: BN, base: String = definedExternally, endian: String = definedExternally)
    constructor(number: BN, base: String = definedExternally)
    constructor(number: Number, endian: String = definedExternally)
    constructor(number: String, endian: String = definedExternally)
    constructor(number: Array<Number>, endian: String = definedExternally)
    constructor(number: Uint8Array, endian: String = definedExternally)
    constructor(number: Buffer, endian: String = definedExternally)
    constructor(number: BN, endian: String = definedExternally)

    open fun fromRed(): BN
    open fun redAdd(b: BN): RedBN
    open fun redIAdd(b: BN): RedBN
    open fun redSub(b: BN): RedBN
    open fun redISub(b: BN): RedBN
    open fun redShl(num: Number): RedBN
    open fun redMul(b: BN): RedBN
    open fun redIMul(b: BN): RedBN
    open fun redSqr(): RedBN
    open fun redISqr(): RedBN
    open fun redSqrt(): RedBN
    open fun redInvm(): RedBN
    open fun redNeg(): RedBN
    open fun redPow(b: BN): RedBN
}
