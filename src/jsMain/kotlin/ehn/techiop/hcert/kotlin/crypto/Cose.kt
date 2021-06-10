package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.*
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array

internal object Cose {
    fun verifySync(signedBitString: ByteArray, pubKey: PubKey<*>): ByteArray {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val key = pubKey.toCoseRepresentation() as CosePublicKey
        val verifier = object : Verifier {
            override val key = key
        }
        return sign.verifySync(Buffer.from(signedBitString.toUint8Array()), verifier).toByteArray()
    }

    fun sign(header: dynamic, input: ByteArray, privKey: PrivKey<*>): Buffer {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val key = privKey.toCoseRepresentation() as CosePrivateKey
        val signer = object : Signer {
            override val key = key
        }
        return sign.createSync(header, Buffer(input.toUint8Array()), signer)
    }
}


