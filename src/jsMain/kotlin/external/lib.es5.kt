@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package tsstdlib

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

external interface PropertyDescriptor {
    var configurable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enumerable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: Any?
        get() = definedExternally
        set(value) = definedExternally
    var writable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    val get: (() -> Any)?
    val set: ((v: Any) -> Unit)?
}

external interface PropertyDescriptorMap {
    @nativeGetter
    operator fun get(s: String): PropertyDescriptor?
    @nativeSetter
    operator fun set(s: String, value: PropertyDescriptor)
}

external interface Object {
    var constructor: Function<*>
    override fun toString(): String
    fun toLocaleString(): String
    fun valueOf(): Any
    fun hasOwnProperty(v: String): Boolean
    fun hasOwnProperty(v: Number): Boolean
    fun hasOwnProperty(v: Any): Boolean
    fun isPrototypeOf(v: Any): Boolean
    fun propertyIsEnumerable(v: String): Boolean
    fun propertyIsEnumerable(v: Number): Boolean
    fun propertyIsEnumerable(v: Any): Boolean
}

external interface ObjectConstructor {
    fun <T, U> assign(target: T, source: U): T /* T & U */
    fun <T, U, V> assign(target: T, source1: U, source2: V): T /* T & U & V */
    fun <T, U, V, W> assign(target: T, source1: U, source2: V, source3: W): T /* T & U & V & W */
    fun assign(target: Any?, vararg sources: Any): Any
    fun getOwnPropertySymbols(o: Any): Array<Any>
    fun keys(o: Any): Array<String>
    fun `is`(value1: Any, value2: Any): Boolean
    fun setPrototypeOf(o: Any, proto: Any?): Any
    @nativeInvoke
    operator fun invoke(): Any
    @nativeInvoke
    operator fun invoke(value: Any): Any
    var prototype: Any
    fun getPrototypeOf(o: Any): Any
    fun getOwnPropertyDescriptor(o: Any, p: String): PropertyDescriptor?
    fun getOwnPropertyDescriptor(o: Any, p: Number): PropertyDescriptor?
    fun getOwnPropertyDescriptor(o: Any, p: Any): PropertyDescriptor?
    fun getOwnPropertyNames(o: Any): Array<String>
    fun create(o: Any?): Any
    fun create(o: Any?, properties: PropertyDescriptorMap /* PropertyDescriptorMap & ThisType<Any> */): Any
    fun defineProperty(o: Any, p: String, attributes: PropertyDescriptor /* PropertyDescriptor & ThisType<Any> */): Any
    fun defineProperty(o: Any, p: Number, attributes: PropertyDescriptor /* PropertyDescriptor & ThisType<Any> */): Any
    fun defineProperty(o: Any, p: Any, attributes: PropertyDescriptor /* PropertyDescriptor & ThisType<Any> */): Any
    fun defineProperties(o: Any, properties: PropertyDescriptorMap /* PropertyDescriptorMap & ThisType<Any> */): Any
    fun <T> seal(o: T): T
    fun <T> freeze(a: Array<T>): Array<T>
    fun <T : Function<*>> freeze(f: T): dynamic
    fun <T> preventExtensions(o: T): T
    fun isSealed(o: Any): Boolean
    fun isFrozen(o: Any): Boolean
    fun isExtensible(o: Any): Boolean
    fun keys(o: Any?): Array<String>
}

external interface DateConstructor {
    @nativeInvoke
    operator fun invoke(): String
    var prototype: Date
    fun parse(s: String): Number
    fun UTC(year: Number, month: Number, date: Number = definedExternally, hours: Number = definedExternally, minutes: Number = definedExternally, seconds: Number = definedExternally, ms: Number = definedExternally): Number
    fun now(): Number
}

external interface ArrayConstructor {
    fun <T> from(iterable: Iterable<T>): Array<T>
    fun <T> from(iterable: ArrayLike<T>): Array<T>
    fun <T, U> from(iterable: Iterable<T>, mapfn: (v: T, k: Number) -> U, thisArg: Any = definedExternally): Array<U>
    fun <T, U> from(iterable: Iterable<T>, mapfn: (v: T, k: Number) -> U): Array<U>
    fun <T, U> from(iterable: ArrayLike<T>, mapfn: (v: T, k: Number) -> U, thisArg: Any = definedExternally): Array<U>
    fun <T, U> from(iterable: ArrayLike<T>, mapfn: (v: T, k: Number) -> U): Array<U>
    fun <T> of(vararg items: T): Array<T>
    @nativeInvoke
    operator fun invoke(arrayLength: Number = definedExternally): Array<Any>
    @nativeInvoke
    operator fun invoke(): Array<Any>
    @nativeInvoke
    operator fun <T> invoke(arrayLength: Number): Array<T>
    @nativeInvoke
    operator fun <T> invoke(vararg items: T): Array<T>
    fun isArray(arg: Any): Boolean
    var prototype: Array<Any>
}

external interface PromiseLike<T> {
    fun then(onfulfilled: ((value: T) -> Any?)? = definedExternally, onrejected: ((reason: Any) -> Any?)? = definedExternally): PromiseLike<dynamic /* TResult1 | TResult2 */>
}

typealias Readonly<T> = Any

external interface ThisType<T>