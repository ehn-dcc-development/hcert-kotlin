@file:JsModule("pkijs/src/common")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package pkijs.src.common

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
import tsstdlib.Crypto
import tsstdlib.SubtleCrypto
import tsstdlib.Algorithm
import Asn1js.Sequence
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import tsstdlib.PromiseLike

external interface Engine {
    var name: String
    var crypto: Crypto
    var subtle: SubtleCrypto
}

external fun setEngine(name: String, crypto: Crypto, subtle: SubtleCrypto)

external fun getEngine(): Engine

external fun getCrypto(): SubtleCrypto?

external fun getRandomValues(view: ArrayBufferView): ArrayBufferView

external fun getOIDByAlgorithm(algorithm: Algorithm): String

external interface `T$8` {
    var algorithm: Algorithm
    var usages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>
}

external fun getAlgorithmParameters(algorithmName: String, operation: String): `T$8`

external fun createCMSECDSASignature(signatureBuffer: ArrayBuffer): ArrayBuffer

external fun stringPrep(inputString: String): String

external fun createECDSASignatureFromCMS(cmsSignature: Sequence): ArrayBuffer

external fun getAlgorithmByOID(oid: String): Algorithm

external fun getHashAlgorithm(signatureAlgorithm: AlgorithmIdentifier): String

external interface `T$9` {
    var counter: Number
    var result: ArrayBuffer
}

external fun kdfWithCounter(hashFunction: String, Zbuffer: ArrayBuffer, Counter: Number, SharedInfo: ArrayBuffer): PromiseLike<`T$9`>

external fun kdf(hashFunction: String, Zbuffer: ArrayBuffer, keydatalen: Number, SharedInfo: ArrayBuffer): PromiseLike<ArrayBuffer>