@file:JsModule("pkijs/src/PrivateKeyInfo")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.PrivateKeyInfo

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
import Asn1js.OctetString
import pkijs.src.Attribute.Attribute
import tsstdlib.JsonWebKey

@JsName("default")
external open class PrivateKeyInfo(params: Any = definedExternally) {
    open var version: Number
    open var privateKeyAlgorithm: Any
    open var privateKey: OctetString
    open var attributes: Array<Attribute>
    open var parsedKey: dynamic /* ECPrivateKey | RSAPrivateKey */
    open fun fromJSON(json: JsonWebKey)
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}