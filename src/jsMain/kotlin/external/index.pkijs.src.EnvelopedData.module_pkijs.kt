@file:JsModule("pkijs/src/EnvelopedData")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.EnvelopedData

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
import pkijs.src.Attribute.Attribute
import pkijs.src.Certificate.Certificate
import pkijs.src.RecipientInfo.RecipientInfo
import tsstdlib.Algorithm
import tsstdlib.PromiseLike

external interface `T$5` {
    var oaepHashAlgorithm: String?
        get() = definedExternally
        set(value) = definedExternally
    var kdfAlgorithm: String?
        get() = definedExternally
        set(value) = definedExternally
    var kekEncryptionLength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$6` {
    var keyIdentifier: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var hmacHashAlgorithm: String?
        get() = definedExternally
        set(value) = definedExternally
    var iterationCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var keyEncryptionAlgorithm: Algorithm?
        get() = definedExternally
        set(value) = definedExternally
    var keyEncryptionAlgorithmParams: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$7` {
    var recipientCertificate: Any
    var recipientPrivateKey: ArrayBuffer
}

@JsName("default")
external open class EnvelopedData(params: Any = definedExternally) {
    open var version: Number
    open var originatorInfo: Any
    open var recipientInfos: Array<RecipientInfo>
    open var encryptedContentInfo: Any
    open var unprotectedAttrs: Array<Attribute>
    open fun addRecipientByCertificate(certificate: Certificate, parameters: `T$5`, variant: Number): Boolean
    open fun addRecipientByPreDefinedData(preDefinedData: ArrayBuffer, parameters: `T$6`, variant: Number): Boolean
    open fun encrypt(contentEncryptionAlgorithm: Algorithm, contentToEncrypt: ArrayBuffer): PromiseLike<ArrayBuffer>
    open fun decrypt(recipientIndex: Number, parameters: `T$7`): PromiseLike<ArrayBuffer>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}