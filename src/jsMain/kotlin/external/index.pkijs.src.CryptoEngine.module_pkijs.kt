@file:JsModule("pkijs/src/CryptoEngine")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.CryptoEngine

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
import tsstdlib.SubtleCrypto
import tsstdlib.JsonWebKey
import tsstdlib.RsaHashedImportParams
import tsstdlib.EcKeyImportParams
import tsstdlib.HmacImportParams
import tsstdlib.DhImportKeyParams
import tsstdlib.PromiseLike
import tsstdlib.CryptoKey
import tsstdlib.Algorithm
import tsstdlib.RsaHashedKeyGenParams
import tsstdlib.EcKeyGenParams
import tsstdlib.DhKeyGenParams
import tsstdlib.AesKeyGenParams
import tsstdlib.HmacKeyGenParams
import tsstdlib.Pbkdf2Params
import tsstdlib.RsaPssParams
import tsstdlib.EcdsaParams
import tsstdlib.AesCmacParams
import tsstdlib.RsaOaepParams
import tsstdlib.AesCtrParams
import tsstdlib.AesCbcParams
import tsstdlib.AesGcmParams
import tsstdlib.AesCfbParams
import tsstdlib.EcdhKeyDeriveParams
import tsstdlib.DhKeyDeriveParams
import tsstdlib.ConcatParams
import tsstdlib.HkdfParams
import tsstdlib.HkdfCtrParams
import tsstdlib.AesKeyAlgorithm

@JsName("default")
external open class CryptoEngine(parameters: Any = definedExternally) : SubtleCrypto {
    open var crypto: SubtleCrypto
    open var name: String
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* PromiseLike */
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: RsaHashedImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* PromiseLike */
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: EcKeyImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* PromiseLike */
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: HmacImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* PromiseLike */
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: DhImportKeyParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBufferView, algorithm: String, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBufferView, algorithm: RsaHashedImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBufferView, algorithm: EcKeyImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBufferView, algorithm: HmacImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBufferView, algorithm: DhImportKeyParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: String, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: RsaHashedImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: EcKeyImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: HmacImportParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    open fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: DhImportKeyParams, extractable: Boolean, keyUsages: Array<String>): dynamic /* PromiseLike */
    override fun exportKey(format: String /* "jwk" | "raw" | "pkcs8" | "spki" */, key: CryptoKey): dynamic /* Promise */
    open fun convert(inputFormat: String, outputFormat: String, keyData: ArrayBufferView, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): PromiseLike<dynamic /* ArrayBufferView | ArrayBuffer | JsonWebKey */>
    open fun convert(inputFormat: String, outputFormat: String, keyData: ArrayBuffer, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): PromiseLike<dynamic /* ArrayBufferView | ArrayBuffer | JsonWebKey */>
    open fun convert(inputFormat: String, outputFormat: String, keyData: JsonWebKey, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): PromiseLike<dynamic /* ArrayBufferView | ArrayBuffer | JsonWebKey */>
    override fun generateKey(algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: RsaHashedKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: EcKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: DhKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: AesKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: HmacKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun generateKey(algorithm: Pbkdf2Params, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    open fun sign(algorithm: String, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun sign(algorithm: String, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun sign(algorithm: RsaPssParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun sign(algorithm: RsaPssParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun sign(algorithm: EcdsaParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun sign(algorithm: EcdsaParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun sign(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun sign(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun unwrapKey(format: String, wrappedKey: ArrayBufferView, unwrappingKey: CryptoKey, unwrapAlgorithm: String, unwrappedKeyAlgorithm: String, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBufferView, unwrappingKey: CryptoKey, unwrapAlgorithm: String, unwrappedKeyAlgorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBufferView, unwrappingKey: CryptoKey, unwrapAlgorithm: Algorithm, unwrappedKeyAlgorithm: String, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBufferView, unwrappingKey: CryptoKey, unwrapAlgorithm: Algorithm, unwrappedKeyAlgorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: String, unwrappedKeyAlgorithm: String, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: String, unwrappedKeyAlgorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: Algorithm, unwrappedKeyAlgorithm: String, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun unwrapKey(format: String, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: Algorithm, unwrappedKeyAlgorithm: Algorithm, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    open fun verify(algorithm: String, key: CryptoKey, signature: ArrayBufferView, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: String, key: CryptoKey, signature: ArrayBuffer, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: RsaPssParams, key: CryptoKey, signature: ArrayBufferView, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: RsaPssParams, key: CryptoKey, signature: ArrayBuffer, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: EcdsaParams, key: CryptoKey, signature: ArrayBufferView, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: EcdsaParams, key: CryptoKey, signature: ArrayBuffer, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: AesCmacParams, key: CryptoKey, signature: ArrayBufferView, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    open fun verify(algorithm: AesCmacParams, key: CryptoKey, signature: ArrayBuffer, data: Any /* ArrayBufferView | ArrayBuffer */): Promise<Boolean>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: String): dynamic /* Promise | PromiseLike */
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: Algorithm): dynamic /* Promise | PromiseLike */
    open fun decrypt(algorithm: String, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: String, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: RsaOaepParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: RsaOaepParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCtrParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCtrParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCbcParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCbcParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesGcmParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesGcmParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCfbParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun decrypt(algorithm: AesCfbParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    override fun deriveBits(algorithm: String, baseKey: CryptoKey, length: Number): dynamic /* Promise | PromiseLike */
    override fun deriveBits(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, length: Number): dynamic /* Promise | PromiseLike */
    override fun deriveBits(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, length: Number): dynamic /* Promise | PromiseLike */
    override fun deriveBits(algorithm: ConcatParams, baseKey: CryptoKey, length: Number): dynamic /* Promise | PromiseLike */
    open fun deriveBits(algorithm: HkdfParams, baseKey: CryptoKey, length: Number): Promise<ArrayBuffer>
    override fun deriveBits(algorithm: Pbkdf2Params, baseKey: CryptoKey, length: Number): dynamic /* Promise | PromiseLike */
    override fun deriveKey(algorithm: String, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params | String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun deriveKey(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params | String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun deriveKey(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params | String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    override fun deriveKey(algorithm: ConcatParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params | String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    open fun deriveKey(algorithm: HkdfParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String>): Promise<CryptoKey>
    override fun deriveKey(algorithm: Pbkdf2Params, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfParams | Pbkdf2Params | String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): dynamic /* Promise | PromiseLike */
    open fun digest(algorithm: String, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun digest(algorithm: String, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun digest(algorithm: Algorithm, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun digest(algorithm: Algorithm, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: String, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: String, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: RsaOaepParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: RsaOaepParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCtrParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCtrParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCbcParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCbcParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCmacParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesGcmParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesGcmParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCfbParams, key: CryptoKey, data: ArrayBufferView): Promise<ArrayBuffer>
    open fun encrypt(algorithm: AesCfbParams, key: CryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
    override fun decrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun decrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun deriveBits(algorithm: Algorithm, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    override fun deriveBits(algorithm: HkdfCtrParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    override fun deriveKey(algorithm: Algorithm, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun deriveKey(algorithm: HkdfCtrParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun digest(algorithm: String, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun digest(algorithm: Algorithm, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun encrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun generateKey(algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<dynamic /* CryptoKeyPair | CryptoKey */>
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: AesKeyAlgorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8ClampedArray, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float64Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: DataView, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun importKey(format: String, keyData: JsonWebKey, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun sign(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun sign(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun sign(algorithm: RsaPssParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun sign(algorithm: EcdsaParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun sign(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8ClampedArray, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float64Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: DataView, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    override fun verify(algorithm: String, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    override fun verify(algorithm: Algorithm, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    override fun verify(algorithm: RsaPssParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    override fun verify(algorithm: EcdsaParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    override fun verify(algorithm: AesCmacParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: RsaOaepParams): PromiseLike<ArrayBuffer>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCtrParams): PromiseLike<ArrayBuffer>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCbcParams): PromiseLike<ArrayBuffer>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCmacParams): PromiseLike<ArrayBuffer>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesGcmParams): PromiseLike<ArrayBuffer>
    override fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCfbParams): PromiseLike<ArrayBuffer>
}