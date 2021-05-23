import React from 'react';
import './App.css';
import {ehn} from "hcert";

let egc = ehn.techiop.hcert.kotlin


function validate() {
// @ts-ignore
    let qr = document.getElementById("tQR").innerText

    let pemCert = "MIIBvTCCAWOgAwIBAgIKAXk8i88OleLsuTAKBggqhkjOPQQDAjA2MRYwFAYDVQQDDA1BVCBER0MgQ1NDQSAxMQswCQYDVQQGEwJBVDEPMA0GA1UECgwGQk1TR1BLMB4XDTIxMDUwNTEyNDEwNloXDTIzMDUwNTEyNDEwNlowPTERMA8GA1UEAwwIQVQgRFNDIDExCzAJBgNVBAYTAkFUMQ8wDQYDVQQKDAZCTVNHUEsxCjAIBgNVBAUTATEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASt1Vz1rRuW1HqObUE9MDe7RzIk1gq4XW5GTyHuHTj5cFEn2Rge37+hINfCZZcozpwQKdyaporPUP1TE7UWl0F3o1IwUDAOBgNVHQ8BAf8EBAMCB4AwHQYDVR0OBBYEFO49y1ISb6cvXshLcp8UUp9VoGLQMB8GA1UdIwQYMBaAFP7JKEOflGEvef2iMdtopsetwGGeMAoGCCqGSM49BAMCA0gAMEUCIQDG2opotWG8tJXN84ZZqT6wUBz9KF8D+z9NukYvnUEQ3QIgdBLFSTSiDt0UJaDF6St2bkUQuVHW6fQbONd731/M4nc="
    let certificate = new egc.crypto.JsCertificate(pemCert);
    console.info(certificate)


    let certRepo = new egc.chain.impl.PrefilledCertificateRepository(certificate)
    let chain = egc.chain.DefaultChain.buildVerificationChain(certRepo)

    console.info(chain)


    let verificationResult = new egc.chain.VerificationResult()
    console.info((verificationResult))
    let greenCertificate = chain.decode(qr, verificationResult)
    // @ts-ignore
    document.getElementById("tRes").innerText = verificationResult.toString()
}


function App() {


    return (
        <div className="App">
            <header className="App-header">
                <textarea id="tQR"/>
                <button onClick={validate}>Validate</button>
                <textarea rows={20} id="tRes"/>
            </header>
        </div>
    );
}

export default App;
