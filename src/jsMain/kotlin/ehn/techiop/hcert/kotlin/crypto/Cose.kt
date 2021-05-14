package ehn.techiop.hcert.kotlin.crypto

import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

object Cose {
    private val buffer = js("var Buffer= extrequire('buffer')")
    private val cose = js("var cose= extrequire('cose-js')")

    fun initVerifier(xCoord: ByteArray, yCoord: ByteArray): dynamic {
        val xc=xCoord.toTypedArray()
        val yc=yCoord.toTypedArray()

        val kx = js("var Buffer= extrequire('buffer');Buffer.Buffer.from(new Uint8Array(xc))")
        val ky = js("var Buffer= extrequire('buffer');Buffer.Buffer.from(new Uint8Array(yc))")
        return initVrfy(kx, ky)
    }

    private fun initVrfy(kx: dynamic, ky: dynamic): dynamic {
        return js("({'key': {'x': kx, 'y': ky}})")
    }

   private  fun vrfy(data: dynamic, verifier: dynamic) {
        (js("var cose= extrequire('cose-js'); cose.sign.verify(data, verifier)") as Promise<Any>).then { console.log("success") }
            .catch { throw it }
    }

     fun verify(cose: ByteArray, verifier: dynamic) {
        val d=cose.toTypedArray()
        val data = js("var Buffer= extrequire('buffer'); Buffer.Buffer.from(new Uint8Array(d))")
        vrfy(data, verifier)
    }
}
