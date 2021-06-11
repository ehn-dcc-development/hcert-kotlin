package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.Signer
import cose.Verifier
import cose.sign
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array

internal object Cose {
    fun verifySync(signedBitString: ByteArray, pubKey: PubKey): ByteArray {
        val key = (pubKey as JsPubKey).toCoseRepresentation()
        val verifier = object : Verifier {
            override val key = key
        }
        return sign.verifySync(Buffer.from(signedBitString.toUint8Array()), verifier).toByteArray()
    }

    fun sign(header: dynamic, input: ByteArray, privKey: PrivKey): Buffer {
        val key = (privKey as JsPrivKey).toCoseRepresentation()
        val signer = object : Signer {
            override val key = key
        }
        return sign.createSync(header, Buffer(input.toUint8Array()), signer)
    }

}


