@file:JsModule("pkijs/src/RSAPrivateKey")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.RSAPrivateKey

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
import Asn1js.Integer
import pkijs.src.OtherPrimeInfo.OtherPrimeInfo
import tsstdlib.JsonWebKey

@JsName("default")
external open class RSAPrivateKey(params: Any = definedExternally) {
    open var version: Number
    open var modulus: Integer
    open var publicExponent: Integer
    open var privateExponent: Integer
    open var prime1: Integer
    open var prime2: Integer
    open var exponent1: Integer
    open var exponent2: Integer
    open var coefficient: Integer
    open var otherPrimeInfos: Array<OtherPrimeInfo>
    open fun fromJSON(json: JsonWebKey)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}