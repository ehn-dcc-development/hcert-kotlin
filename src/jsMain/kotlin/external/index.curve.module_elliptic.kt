@file:JsQualifier("curve")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package curve

import BN
import kotlin.js.*
import org.khronos.webgl.*
import Buffer

external open class base(type: String, conf: BaseCurveOptions) {
    open var p: Any
    open var type: String
    open var red: Any
    open var zero: Any
    open var one: Any
    open var two: Any
    open var n: Any
    open var g: BasePoint
    open var redN: Any
    open fun validate(point: BasePoint): Boolean
    open fun decodePoint(bytes: Buffer, enc: String /* "hex" */ = definedExternally): BasePoint
    open fun decodePoint(bytes: Buffer): BasePoint
    open fun decodePoint(bytes: String, enc: String /* "hex" */ = definedExternally): BasePoint
    open fun decodePoint(bytes: String): BasePoint
    open class BasePoint(curve: base, type: String) {
        open var curve: base
        open var type: String
        open var precomputed: PrecomputedValues?
        open fun encode(enc: String /* "hex" | "array" */, compact: Boolean): dynamic /* String | Array */
        open fun encodeCompressed(enc: String /* "hex" | "array" */): dynamic /* String | Array */
        open fun encodeCompressed(): Array<Number>
        open fun validate(): Boolean
        open fun precompute(power: Number): BasePoint
        open fun dblp(k: Number): BasePoint
        open fun inspect(): String
        open fun isInfinity(): Boolean
        open fun add(p: BasePoint): BasePoint
        open fun mul(k: BN): BasePoint
        open fun dbl(): BasePoint
        open fun getX(): BN
        open fun getY(): BN
        open fun eq(p: BasePoint): Boolean
        open fun neg(): BasePoint
    }
    interface BaseCurveOptions {
        var p: dynamic /* Number | String | Array<Number> | Buffer | BN */
            get() = definedExternally
            set(value) = definedExternally
        var prime: dynamic /* BN? | String? */
            get() = definedExternally
            set(value) = definedExternally
        var n: dynamic /* Number? | BN? | Buffer? */
            get() = definedExternally
            set(value) = definedExternally
        var g: BasePoint?
            get() = definedExternally
            set(value) = definedExternally
        var gRed: Any?
            get() = definedExternally
            set(value) = definedExternally
    }
    interface PrecomputedValues {
        var doubles: Any
        var naf: Any
        var beta: Any
    }
}

external open class edwards(conf: EdwardsConf) : base {
    open var a: Any
    open var c: Any
    open var c2: Any
    open var d: Any
    open var dd: Any
    open fun point(x: String, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: String, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: String, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: BN, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: BN, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: BN, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Number, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Number, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: Number, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Buffer, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Buffer, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: Buffer, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Uint8Array, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Uint8Array, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: Uint8Array, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Array<Number>, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally, t: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun point(x: Array<Number>, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): EdwardsPoint
    open fun point(x: Array<Number>, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, z: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */ = definedExternally): EdwardsPoint
    open fun pointFromX(x: String, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: String): EdwardsPoint
    open fun pointFromX(x: BN, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: BN): EdwardsPoint
    open fun pointFromX(x: Number, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: Number): EdwardsPoint
    open fun pointFromX(x: Buffer, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: Buffer): EdwardsPoint
    open fun pointFromX(x: Uint8Array, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: Uint8Array): EdwardsPoint
    open fun pointFromX(x: Array<Number>, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromX(x: Array<Number>): EdwardsPoint
    open fun pointFromY(y: String, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: String): EdwardsPoint
    open fun pointFromY(y: BN, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: BN): EdwardsPoint
    open fun pointFromY(y: Number, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: Number): EdwardsPoint
    open fun pointFromY(y: Buffer, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: Buffer): EdwardsPoint
    open fun pointFromY(y: Uint8Array, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: Uint8Array): EdwardsPoint
    open fun pointFromY(y: Array<Number>, odd: Boolean = definedExternally): EdwardsPoint
    open fun pointFromY(y: Array<Number>): EdwardsPoint
    open fun pointFromJSON(obj: Array<Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */>): EdwardsPoint
    interface EdwardsConf : BaseCurveOptions {
        var a: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
            get() = definedExternally
            set(value) = definedExternally
        var c: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
            get() = definedExternally
            set(value) = definedExternally
        var d: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
            get() = definedExternally
            set(value) = definedExternally
    }
    open class EdwardsPoint(curve: base, type: String) : BasePoint {
        open var x: Any
        open var y: Any
        open var z: Any
        open var t: Any
        open fun normalize(): EdwardsPoint
        open fun eqXToP(x: BN): Boolean
    }
}

external open class short(conf: ShortConf) : base {
    open var a: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
    open var b: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
    override var g: BasePoint
    open fun point(x: String, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: String, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun point(x: BN, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: BN, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun point(x: Number, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: Number, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun point(x: Buffer, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: Buffer, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun point(x: Uint8Array, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: Uint8Array, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun point(x: Array<Number>, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */, isRed: Boolean = definedExternally): ShortPoint
    open fun point(x: Array<Number>, y: Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */): ShortPoint
    open fun pointFromX(x: String, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: String): ShortPoint
    open fun pointFromX(x: BN, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: BN): ShortPoint
    open fun pointFromX(x: Number, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: Number): ShortPoint
    open fun pointFromX(x: Buffer, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: Buffer): ShortPoint
    open fun pointFromX(x: Uint8Array, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: Uint8Array): ShortPoint
    open fun pointFromX(x: Array<Number>, odd: Boolean = definedExternally): ShortPoint
    open fun pointFromX(x: Array<Number>): ShortPoint
    open fun pointFromJSON(obj: Array<Any /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */>, red: Boolean): ShortPoint
    interface ShortConf : BaseCurveOptions {
        var a: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
            get() = definedExternally
            set(value) = definedExternally
        var b: dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */
            get() = definedExternally
            set(value) = definedExternally
        var beta: dynamic /* String? | BN? | Number? | Buffer? | Uint8Array? | ReadonlyArray<Number>? */
            get() = definedExternally
            set(value) = definedExternally
        var lambda: dynamic /* String? | BN? | Number? | Buffer? | Uint8Array? | ReadonlyArray<Number>? */
            get() = definedExternally
            set(value) = definedExternally
    }
    open class ShortPoint(curve: base, type: String) : BasePoint {
        open var x: Any?
        open var y: Any?
        open var inf: Boolean
        open fun toJSON(): Array<dynamic /* String | BN | Number | Buffer | Uint8Array | ReadonlyArray<Number> */>
    }
}