@file:JsModule("elliptic")
@file:JsNonModule

package elliptic

import BN
import Buffer
import org.khronos.webgl.Uint8Array

external class Signature {
    fun toDER(): Array<Byte>
}

external class EcPublicKey {
    fun getX(): BN
    fun getY(): BN
}

external class EcKeyPair {
    fun getPublic(): EcPublicKey
    fun getPrivate(): BN
    val ec: EC
}

@JsName("ec")
external class EC(curve: String) {
    fun genKeyPair(): EcKeyPair

    fun keyFromPrivate(buffer: Buffer): EcKeyPair

    fun sign(msg: Uint8Array, key: BN): Signature
}