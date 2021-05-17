@file:JsModule("pkijs/src/TimeStampResp")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.TimeStampResp

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
import tsstdlib.CryptoKey
import tsstdlib.PromiseLike
import pkijs.src.SignedData.VerifyParams
import pkijs.src.SignedData.VerifyResult

@JsName("default")
external open class TimeStampResp(params: Any = definedExternally) {
    open var status: Any
    open var timeStampToken: Any
    open fun sign(privateKey: CryptoKey, hashAlgorithm: String = definedExternally): PromiseLike<ArrayBuffer>
    open fun verify(verificationParameters: VerifyParams): PromiseLike<VerifyResult>
    open fun fromSchema(schema: Any)
    open fun toSchema(): Any
    open fun toJSON(): Any

    companion object {
        fun compareWithDefault(memberName: String, memberValue: Any): Boolean
        fun defaultValues(memberName: String): Any
        fun schema(parameters: Any = definedExternally): Any
    }
}