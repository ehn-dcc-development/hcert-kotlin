@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*

@JsModule("node-rsa")
external open class NodeRSA {
    constructor(key: KeyBits = definedExternally)
    constructor()
    constructor(key: String, format: String = definedExternally, options: Options = definedExternally)
    constructor(key: String)
    constructor(key: String, format: String = definedExternally)
    constructor(key: Buffer, format: String = definedExternally, options: Options = definedExternally)
    constructor(key: Buffer)
    constructor(key: Buffer, format: String = definedExternally)
    constructor(key: KeyComponentsPrivate, format: String = definedExternally, options: Options = definedExternally)
    constructor(key: KeyComponentsPrivate)
    constructor(key: KeyComponentsPrivate, format: String = definedExternally)
    constructor(key: KeyComponentsPublic, format: String = definedExternally, options: Options = definedExternally)
    constructor(key: KeyComponentsPublic)
    constructor(key: KeyComponentsPublic, format: String = definedExternally)
    open fun setOptions(options: Options)
    open fun generateKeyPair(bits: Number = definedExternally, exponent: Number = definedExternally): NodeRSA
    open fun importKey(key: String, format: String /* "private" | "public" | "pkcs1" | "pkcs1-pem" | "pkcs1-private" | "pkcs1-private-pem" | "pkcs1-public" | "pkcs1-public-pem" | "pkcs8" | "pkcs8-pem" | "pkcs8-private" | "pkcs8-private-pem" | "pkcs8-public" | "pkcs8-public-pem" | "openssh-public" | "openssh-private" | "pkcs1-der" | "pkcs1-private-der" | "pkcs1-public-der" | "pkcs8-der" | "pkcs8-private-der" | "pkcs8-public-der" | "components" | "components-pem" | "components-der" | "components-private" | "components-private-pem" | "components-private-der" | "components-public" | "components-public-pem" | "components-public-der" */ = definedExternally): NodeRSA
    open fun importKey(key: String): NodeRSA
    open fun importKey(key: Buffer, format: String /* "private" | "public" | "pkcs1" | "pkcs1-pem" | "pkcs1-private" | "pkcs1-private-pem" | "pkcs1-public" | "pkcs1-public-pem" | "pkcs8" | "pkcs8-pem" | "pkcs8-private" | "pkcs8-private-pem" | "pkcs8-public" | "pkcs8-public-pem" | "openssh-public" | "openssh-private" | "pkcs1-der" | "pkcs1-private-der" | "pkcs1-public-der" | "pkcs8-der" | "pkcs8-private-der" | "pkcs8-public-der" | "components" | "components-pem" | "components-der" | "components-private" | "components-private-pem" | "components-private-der" | "components-public" | "components-public-pem" | "components-public-der" */ = definedExternally): NodeRSA
    open fun importKey(key: Buffer): NodeRSA
    open fun importKey(key: KeyComponentsPrivate, format: String /* "private" | "public" | "pkcs1" | "pkcs1-pem" | "pkcs1-private" | "pkcs1-private-pem" | "pkcs1-public" | "pkcs1-public-pem" | "pkcs8" | "pkcs8-pem" | "pkcs8-private" | "pkcs8-private-pem" | "pkcs8-public" | "pkcs8-public-pem" | "openssh-public" | "openssh-private" | "pkcs1-der" | "pkcs1-private-der" | "pkcs1-public-der" | "pkcs8-der" | "pkcs8-private-der" | "pkcs8-public-der" | "components" | "components-pem" | "components-der" | "components-private" | "components-private-pem" | "components-private-der" | "components-public" | "components-public-pem" | "components-public-der" */ = definedExternally): NodeRSA
    open fun importKey(key: KeyComponentsPrivate): NodeRSA
    open fun importKey(key: KeyComponentsPublic, format: String /* "private" | "public" | "pkcs1" | "pkcs1-pem" | "pkcs1-private" | "pkcs1-private-pem" | "pkcs1-public" | "pkcs1-public-pem" | "pkcs8" | "pkcs8-pem" | "pkcs8-private" | "pkcs8-private-pem" | "pkcs8-public" | "pkcs8-public-pem" | "openssh-public" | "openssh-private" | "pkcs1-der" | "pkcs1-private-der" | "pkcs1-public-der" | "pkcs8-der" | "pkcs8-private-der" | "pkcs8-public-der" | "components" | "components-pem" | "components-der" | "components-private" | "components-private-pem" | "components-private-der" | "components-public" | "components-public-pem" | "components-public-der" */ = definedExternally): NodeRSA
    open fun importKey(key: KeyComponentsPublic): NodeRSA
    open fun exportKey(format: String /* "private" | "public" | "pkcs1" | "pkcs1-pem" | "pkcs1-private" | "pkcs1-private-pem" | "pkcs1-public" | "pkcs1-public-pem" | "pkcs8" | "pkcs8-pem" | "pkcs8-private" | "pkcs8-private-pem" | "pkcs8-public" | "pkcs8-public-pem" | "openssh-public" | "openssh-private" | "pkcs1-der" | "pkcs1-private-der" | "pkcs1-public-der" | "pkcs8-der" | "pkcs8-private-der" | "pkcs8-public-der" | "components" | "components-pem" | "components-der" | "components-private" | "components-private-pem" | "components-private-der" | "components-public" | "components-public-pem" | "components-public-der" */ = definedExternally): dynamic /* KeyComponentsPublic */
    open fun exportKey(): String
    open fun isPrivate(): Boolean
    open fun isPublic(strict: Boolean = definedExternally): Boolean
    open fun isEmpty(): Boolean
    open fun getKeySize(): Number
    open fun getMaxMessageSize(): Number
    open fun encrypt(data: String?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encrypt(data: String?): Buffer
    open fun encrypt(data: Any?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encrypt(data: Any?): Buffer
    open fun encrypt(data: Array<Any>?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encrypt(data: Array<Any>?): Buffer
    open fun encrypt(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */, sourceEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encrypt(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): dynamic /* Buffer | String */
    open fun encryptPrivate(data: String?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encryptPrivate(data: String?): Buffer
    open fun encryptPrivate(data: Any?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encryptPrivate(data: Any?): Buffer
    open fun encryptPrivate(data: Array<Any>?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encryptPrivate(data: Array<Any>?): Buffer
    open fun encryptPrivate(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */, sourceEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun encryptPrivate(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): dynamic /* Buffer | String */
    open fun decrypt(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun decrypt(data: Buffer): Buffer
    open fun decrypt(data: String, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun decrypt(data: String): Buffer
    open fun <T : Any?> decrypt(data: Buffer, encoding: String /* "json" */): T
    open fun <T : Any?> decrypt(data: String, encoding: String /* "json" */): T
    open fun decryptPublic(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun decryptPublic(data: Buffer): Buffer
    open fun decryptPublic(data: String, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun decryptPublic(data: String): Buffer
    open fun <T : Any?> decryptPublic(data: Buffer, encoding: String /* "json" */): T
    open fun <T : Any?> decryptPublic(data: String, encoding: String /* "json" */): T
    open fun sign(data: String?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun sign(data: String?): Buffer
    open fun sign(data: Any?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun sign(data: Any?): Buffer
    open fun sign(data: Array<Any>?, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun sign(data: Array<Any>?): Buffer
    open fun sign(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */, sourceEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): dynamic /* Buffer | String */
    open fun sign(data: Buffer, encoding: String /* "buffer" | "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): dynamic /* Buffer | String */
    open fun verify(data: String?, signature: Buffer): Boolean
    open fun verify(data: Any?, signature: Buffer): Boolean
    open fun verify(data: Array<Any>?, signature: Buffer): Boolean
    open fun verify(data: Buffer, signature: Buffer, sourceEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */ = definedExternally): Boolean
    open fun verify(data: Buffer, signature: Buffer): Boolean
    open fun verify(data: Buffer, signature: String, sourceEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */, signatureEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): Boolean
    open fun verify(data: String?, signature: String, sourceEncoding: Nothing?, signatureEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): Boolean
    open fun verify(data: Any?, signature: String, sourceEncoding: Nothing?, signatureEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): Boolean
    open fun verify(data: Array<Any>?, signature: String, sourceEncoding: Nothing?, signatureEncoding: String /* "ascii" | "utf8" | "utf16le" | "ucs2" | "latin1" | "base64" | "hex" | "binary" | "buffer" */): Boolean
    interface KeyComponentsPrivate {
        var n: Buffer
        var e: dynamic /* Buffer | Number */
            get() = definedExternally
            set(value) = definedExternally
        var d: Buffer
        var p: Buffer
        var q: Buffer
        var dmp1: Buffer
        var dmq1: Buffer
        var coeff: Buffer
    }
    interface KeyComponentsPublic {
        var n: Buffer
        var e: dynamic /* Buffer | Number */
            get() = definedExternally
            set(value) = definedExternally
    }
    interface KeyBits {
        var b: Number
    }
    interface AdvancedEncryptionSchemePKCS1 {
        var scheme: String /* "pkcs1" */
        var padding: Number
    }
    interface AdvancedEncryptionSchemePKCS1OAEP {
        var scheme: String /* "pkcs1_oaep" */
        var hash: String /* "ripemd160" | "md4" | "md5" | "sha1" | "sha224" | "sha256" | "sha384" | "sha512" */
        val mgf: ((data: Buffer, length: Number, hash: String /* "ripemd160" | "md4" | "md5" | "sha1" | "sha224" | "sha256" | "sha384" | "sha512" */) -> Buffer)?
    }
    interface AdvancedSigningSchemePSS {
        var scheme: String /* "pss" */
        var hash: String /* "ripemd160" | "md4" | "md5" | "sha1" | "sha224" | "sha256" | "sha384" | "sha512" */
        var saltLength: Number
    }
    interface AdvancedSigningSchemePKCS1 {
        var scheme: String /* "pkcs1" */
        var hash: String /* "ripemd160" | "md4" | "md5" | "sha1" | "sha224" | "sha256" | "sha384" | "sha512" */
    }
    interface Options {
        var environment: String? /* "browser" | "node" */
            get() = definedExternally
            set(value) = definedExternally
        var encryptionScheme: dynamic /* "pkcs1_oaep" | "pkcs1" | AdvancedEncryptionSchemePKCS1? | AdvancedEncryptionSchemePKCS1OAEP? */
            get() = definedExternally
            set(value) = definedExternally
        var signingScheme: dynamic /* "pkcs1" | "pss" | "pkcs1-ripemd160" | "pkcs1-md4" | "pkcs1-md5" | "pkcs1-sha" | "pkcs1-sha1" | "pkcs1-sha224" | "pkcs1-sha256" | "pkcs1-sha384" | "pkcs1-sha512" | "pss-ripemd160" | "pss-md4" | "pss-md5" | "pss-sha" | "pss-sha1" | "pss-sha224" | "pss-sha256" | "pss-sha384" | "pss-sha512" | AdvancedSigningSchemePSS? | AdvancedSigningSchemePKCS1? */
            get() = definedExternally
            set(value) = definedExternally
    }
}