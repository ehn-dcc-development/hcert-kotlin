@file:JsModule("pkijs/src/SignedData")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.SignedData

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
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.Certificate.Certificate
import pkijs.src.SignerInfo.SignerInfo
import tsstdlib.PromiseLike
import tsstdlib.CryptoKey

external interface VerifyParams {
    var signer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var data: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var trustedCerts: Array<Certificate>?
        get() = definedExternally
        set(value) = definedExternally
    var checkDate: Date?
        get() = definedExternally
        set(value) = definedExternally
    var checkChain: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var includeSignerCertificate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var extendedMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface VerifyResult {
    var date: Date
    var code: Number
    var message: String
    var signatureVerified: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var signerCertificate: Any?
        get() = definedExternally
        set(value) = definedExternally
    var signerCertificateVerified: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
external open class SignedData(params: Any = definedExternally) {
    open var version: Number
    open var digestAlgorithms: Array<AlgorithmIdentifier>
    open var encapContentInfo: Any
    open var certificates: dynamic /* Array<Certificate> | Array<OtherCertificateFormat> */
    open var crls: dynamic /* Array<CertificateRevocationList> | Array<OtherRevocationInfoFormat> */
    open var signerInfos: Array<SignerInfo>
    open fun toSchema(encodeFlag: Boolean = definedExternally): Any
    open fun verify(options: VerifyParams): PromiseLike<VerifyResult>
    open fun sign(privateKey: CryptoKey, signerIndex: Number, hashAlgorithm: String = definedExternally, data: ArrayBufferView = definedExternally): ArrayBuffer
    open fun sign(privateKey: CryptoKey, signerIndex: Number): ArrayBuffer
    open fun sign(privateKey: CryptoKey, signerIndex: Number, hashAlgorithm: String = definedExternally): ArrayBuffer
    open fun sign(privateKey: CryptoKey, signerIndex: Number, hashAlgorithm: String = definedExternally, data: ArrayBuffer = definedExternally): ArrayBuffer
    open fun fromSchema(schema: Any)
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}