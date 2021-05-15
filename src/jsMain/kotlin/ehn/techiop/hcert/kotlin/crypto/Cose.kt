package ehn.techiop.hcert.kotlin.crypto

import kotlin.js.Promise

object Cose {

    private fun vrfy(data: dynamic, verifier: dynamic, onSuccess: () -> Unit) {
        (js("var cose= extrequire('cose-js'); cose.sign.verify(data, verifier)") as Promise<Any>).then { onSuccess.invoke() }
            .catch { throw it }
    }

    fun verify(signedBitString: ByteArray, pubKey: CoseJsEcPubKey, onSuccess: () -> Unit) {
        val d = signedBitString.toTypedArray()
        val data = js("var Buffer= extrequire('buffer'); Buffer.Buffer.from(new Uint8Array(d))")
        vrfy(data, pubKey.toCoseRepresenation(), onSuccess)
    }
}
