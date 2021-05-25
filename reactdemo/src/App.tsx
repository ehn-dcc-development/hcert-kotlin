import React from 'react';
import './App.css';
import {Verifier} from "hcert";



function validate() {
    // @ts-ignore
    let qr = document.getElementById("tQR").textContent
    // @ts-ignore
    let pemCert = document.getElementById("tCert").textContent

    let verifier = new Verifier([pemCert]);

    let result =  verifier.verify(qr);
    console.debug(result);
    // @ts-ignore
    document.getElementById("tRes").textContent = JSON.stringify(result, null, 2);
    // @ts-ignore
    document.getElementById("tDec").value = verifier.decide(result.verificationResult);
    // @ts-ignore
    document.getElementById("tEgc").value = JSON.stringify(result.greenCertificate,null,2);
}


function App() {


    // @ts-ignore
    return (
        <div className="App">
            <header className="App-header">
              <p>
                  PEM-Encoded Certificate<br/>
                  <textarea rows={10} cols={100} id="tCert">MIIBvTCCAWOgAwIBAgIKAXk8i88OleLsuTAKBggqhkjOPQQDAjA2MRYwFAYDVQQDDA1BVCBER0MgQ1NDQSAxMQswCQYDVQQGEwJBVDEPMA0GA1UECgwGQk1TR1BLMB4XDTIxMDUwNTEyNDEwNloXDTIzMDUwNTEyNDEwNlowPTERMA8GA1UEAwwIQVQgRFNDIDExCzAJBgNVBAYTAkFUMQ8wDQYDVQQKDAZCTVNHUEsxCjAIBgNVBAUTATEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASt1Vz1rRuW1HqObUE9MDe7RzIk1gq4XW5GTyHuHTj5cFEn2Rge37+hINfCZZcozpwQKdyaporPUP1TE7UWl0F3o1IwUDAOBgNVHQ8BAf8EBAMCB4AwHQYDVR0OBBYEFO49y1ISb6cvXshLcp8UUp9VoGLQMB8GA1UdIwQYMBaAFP7JKEOflGEvef2iMdtopsetwGGeMAoGCCqGSM49BAMCA0gAMEUCIQDG2opotWG8tJXN84ZZqT6wUBz9KF8D+z9NukYvnUEQ3QIgdBLFSTSiDt0UJaDF6St2bkUQuVHW6fQbONd731/M4nc=</textarea>
              </p>
              <p>
                  QR Code Contents<br/>
                  <textarea rows={10} cols={100} id="tQR">HC1:NCFTW2H:7*I06R3W/J:O6:P4QB3+7RKFVJWV66UBCE//UXDT:*ML-4D.NBXR+SRHMNIY6EB8I595+6UY9-+0DPIO6C5%0SBHN-OWKCJ6BLC2M.M/NPKZ4F3WNHEIE6IO26LB8:F4:JVUGVY8*EKCLQ..QCSTS+F$:0PON:.MND4Z0I9:GU.LBJQ7/2IJPR:PAJFO80NN0TRO1IB:44:N2336-:KC6M*2N*41C42CA5KCD555O/A46F6ST1JJ9D0:.MMLH2/G9A7ZX4DCL*010LGDFI$MUD82QXSVH6R.CLIL:T4Q3129HXB8WZI8RASDE1LL9:9NQDC/O3X3G+A:2U5VP:IE+EMG40R53CG9J3JE1KB KJA5*$4GW54%LJBIWKE*HBX+4MNEIAD$3NR E228Z9SS4E R3HUMH3J%-B6DRO3T7GJBU6O URY858P0TR8MDJ$6VL8+7B5$G CIKIPS2CPVDK%K6+N0GUG+TG+RB5JGOU55HXDR.TL-N75Y0NHQTZ3XNQMTF/ZHYBQ$8IR9MIQHOSV%9K5-7%ZQ/.15I0*-J8AVD0N0/0USH.3</textarea>
              </p>
                <p>
                    <button onClick={validate}>Validate</button>
                </p>
                <p>
                    Verification Result Structure<br/>
                    <textarea rows={10} cols={100} id="tRes"/>
                </p><p>
                    Static Decision<br/>
                    <textarea rows={10} cols={100} id="tDec"/>
                </p><p>
                    Green Pass Structure<br/>
                    <textarea rows={10} cols={100} id="tEgc"/>
                </p>
            </header>
        </div>
    );
}

export default App;
