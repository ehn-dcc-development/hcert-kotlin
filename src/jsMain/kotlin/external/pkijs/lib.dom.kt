@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package tsstdlib

import org.khronos.webgl.Uint8Array

external interface AesCbcParams : Algorithm {
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesCtrParams : Algorithm {
    var counter: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number
}

external interface AesDerivedKeyParams : Algorithm {
    var length: Number
}

external interface AesGcmParams : Algorithm {
    var additionalData: dynamic /* Int8Array? | Int16Array? | Int32Array? | Uint8Array? | Uint16Array? | Uint32Array? | Uint8ClampedArray? | Float32Array? | Float64Array? | DataView? | ArrayBuffer? */
        get() = definedExternally
        set(value) = definedExternally
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var tagLength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesKeyAlgorithm : KeyAlgorithm {
    var length: Number
}

external interface AesKeyGenParams : Algorithm {
    var length: Number
}

external interface Algorithm {
    var name: String
}

external interface EcKeyGenParams : Algorithm {
    var namedCurve: NamedCurve
}

external interface EcKeyImportParams : Algorithm {
    var namedCurve: NamedCurve
}

external interface EcdhKeyDeriveParams : Algorithm {
    var public: CryptoKey
}

external interface EcdsaParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface HkdfParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var info: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var salt: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface HmacImportParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HmacKeyGenParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface JsonWebKey {
    var alg: String?
        get() = definedExternally
        set(value) = definedExternally
    var crv: String?
        get() = definedExternally
        set(value) = definedExternally
    var d: String?
        get() = definedExternally
        set(value) = definedExternally
    var dp: String?
        get() = definedExternally
        set(value) = definedExternally
    var dq: String?
        get() = definedExternally
        set(value) = definedExternally
    var e: String?
        get() = definedExternally
        set(value) = definedExternally
    var ext: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var k: String?
        get() = definedExternally
        set(value) = definedExternally
    var key_ops: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var kty: String?
        get() = definedExternally
        set(value) = definedExternally
    var n: String?
        get() = definedExternally
        set(value) = definedExternally
    var oth: Array<RsaOtherPrimesInfo>?
        get() = definedExternally
        set(value) = definedExternally
    var p: String?
        get() = definedExternally
        set(value) = definedExternally
    var q: String?
        get() = definedExternally
        set(value) = definedExternally
    var qi: String?
        get() = definedExternally
        set(value) = definedExternally
    var use: String?
        get() = definedExternally
        set(value) = definedExternally
    var x: String?
        get() = definedExternally
        set(value) = definedExternally
    var y: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface KeyAlgorithm {
    var name: String
}

external interface Pbkdf2Params : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var iterations: Number
    var salt: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaHashedImportParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaHashedKeyGenParams : RsaKeyGenParams {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaKeyGenParams : Algorithm {
    var modulusLength: Number
    var publicExponent: BigInteger
}

external interface RsaOaepParams : Algorithm {
    var label: dynamic /* Int8Array? | Int16Array? | Int32Array? | Uint8Array? | Uint16Array? | Uint32Array? | Uint8ClampedArray? | Float32Array? | Float64Array? | DataView? | ArrayBuffer? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaOtherPrimesInfo {
    var d: String?
        get() = definedExternally
        set(value) = definedExternally
    var r: String?
        get() = definedExternally
        set(value) = definedExternally
    var t: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaPssParams : Algorithm {
    var saltLength: Number
}

external interface AesCfbParams : Algorithm {
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesCmacParams : Algorithm {
    var length: Number
}

external interface ConcatParams : Algorithm {
    var algorithmId: Uint8Array
    var hash: dynamic /* String? | Algorithm? */
        get() = definedExternally
        set(value) = definedExternally
    var partyUInfo: Uint8Array
    var partyVInfo: Uint8Array
    var privateInfo: Uint8Array?
        get() = definedExternally
        set(value) = definedExternally
    var publicInfo: Uint8Array?
        get() = definedExternally
        set(value) = definedExternally
}

/*
external interface Crypto {
    var subtle: SubtleCrypto
    fun <T> getRandomValues(array: T): T
}
*/
external interface CryptoKey {
    var algorithm: KeyAlgorithm
    var extractable: Boolean
    var type: String /* "private" | "public" | "secret" */
    var usages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>
}

external interface CryptoKeyPair {
    var privateKey: CryptoKey
    var publicKey: CryptoKey
}

external interface DhImportKeyParams : Algorithm {
    var generator: Uint8Array
    var prime: Uint8Array
}

external interface DhKeyDeriveParams : Algorithm {
    var public: CryptoKey
}

external interface DhKeyGenParams : Algorithm {
    var generator: Uint8Array
    var prime: Uint8Array
}

external interface HkdfCtrParams : Algorithm {
    var context: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var hash: dynamic /* String | Algorithm */
        get() = definedExternally
        set(value) = definedExternally
    var label: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}
/*
external interface SubtleCrypto {
    fun decrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: String, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: Algorithm, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: ConcatParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: HkdfCtrParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: Pbkdf2Params, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveKey(algorithm: String, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: Algorithm, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: ConcatParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: HkdfCtrParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: Pbkdf2Params, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun digest(algorithm: String, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun digest(algorithm: Algorithm, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun exportKey(format: String /* "jwk" | "raw" | "pkcs8" | "spki" */, key: CryptoKey): dynamic /* PromiseLike */
    fun generateKey(algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<dynamic /* CryptoKeyPair | CryptoKey */>
    fun generateKey(algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<dynamic /* CryptoKeyPair | CryptoKey */>
    fun generateKey(algorithm: RsaHashedKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: EcKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: DhKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: AesKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun generateKey(algorithm: HmacKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun generateKey(algorithm: Pbkdf2Params, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: RsaHashedImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: EcKeyImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: HmacImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: DhImportKeyParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: AesKeyAlgorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8ClampedArray, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float64Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: DataView, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String, keyData: JsonWebKey, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun sign(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: RsaPssParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: EcdsaParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8ClampedArray, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float64Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: DataView, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun verify(algorithm: String, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: Algorithm, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: RsaPssParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: EcdsaParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: AesCmacParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: String): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: Algorithm): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: RsaOaepParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCtrParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCbcParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCmacParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesGcmParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCfbParams): PromiseLike<ArrayBuffer>
}
*/
typealias BigInteger = Uint8Array

typealias NamedCurve = String