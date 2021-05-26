@file:JsModule("base64url")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

external interface Base64Url {
    @nativeInvoke
    operator fun invoke(input: String, encoding: String = definedExternally): String
    @nativeInvoke
    operator fun invoke(input: String): String
    @nativeInvoke
    operator fun invoke(input: Buffer, encoding: String = definedExternally): String
    @nativeInvoke
    operator fun invoke(input: Buffer): String
    fun encode(input: String, encoding: String = definedExternally): String
    fun encode(input: String): String
    fun encode(input: Buffer, encoding: String = definedExternally): String
    fun encode(input: Buffer): String
    fun decode(base64url: String, encoding: String = definedExternally): String
    fun toBase64(base64url: String): String
    fun toBase64(base64url: Buffer): String
    fun fromBase64(base64: String): String
    fun toBuffer(base64url: String): Buffer
}

@JsName("default")
external var base64url: Base64Url