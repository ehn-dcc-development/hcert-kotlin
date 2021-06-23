# Electronic Health Certificate Kotlin Multiplatform Library

This library implements a very basic validation and creation chain for electronic health certificates (HCERT):
 - Encode in CBOR
 - Wrap in a CWT structure
 - Sign and embed in COSE
 - Compress with ZLib
 - Encode in Base45
 - Prepend with Context Identifier
 - Encode as QR Code

All services are implemented according to <https://github.com/ehn-digital-green-development/hcert-spec>, Version 1.0.5 from 2021-04-18.

The schemata for data classes are imported from <https://github.com/ehn-digital-green-development/ehn-dgc-schema>, up to Version 1.3.0, from 2021-06-11.

Several other git repositories are included as submodules. Please clone this repository with `git clone --recursive` or run `git submodule init && git submodule update --recursive` afterwards.

This Kotlin library is a [mulitplatform project](https://kotlinlang.org/docs/multiplatform.html), with targets for JVM and JavaScript.

## Usage (JVM)

`ehn.techiop.hcert.kotlin.chain.Chain` is the main class for encoding and decoding HCERT data. For encoding, pass an instance of a `GreenCertificate` (data class conforming to the JSON schema) and get a `ChainResult`. That object will contain all revelant intermediate results as well as the final result (`step5Prefixed`). This final result can be passed to a `DefaultTwoDimCodeService` that will encode it as a 2D QR Code.

The usage of interfaces for all services (CBOR, CWT, COSE, ZLib, Context) in the chain may seem over-engineered at first, but it allows us to create wrongly encoded results, by passing faulty implementations of the service. Those services reside in the namespace `ehn.techiop.hcert.kotlin.chain.faults` and should, obviously, not be used for production code.

The actual, correct, implementations of the service interfaces reside in `ehn.techiop.hcert.kotlin.chain.impl`. These "default" implementations will be used when the chain is constructed using `DefaultChain.buildCreationChain()` or `DefaultChain.buildVerificationChain()`.


Example for creation services:

```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
Chain chain = DefaultChain.buildCreationChain(cryptoService);

// Load the input data from somewhere ...
String json = "{ \"sub\": { \"gn\": \"Gabriele\", ...";
String input = Json.decodeFromString<GreenCertificate>(json);

// Apply all encoding steps from the Chain: CBOR, CWT, COSE, ZLIB, Base45, Context
ChainResult result = chain.encode(input);

// Optionally encode it as a QR-Code with 350 pixel in width and height
TwoDimCodeService qrCodeService = new DefaultTwoDimCodeService(350);
String encodedImage = qrCodeService.encode(result.step5Prefixed);
String encodedBase64QrCode = Base64.getEncoder().encodeToString(encodedImage);

// Then include in an HTML page or something ...
String html = "<img src=\"data:image/png;base64," + encodedBase64QrCode + "\" />";
```

Example for the verification side, i.e. in apps:

```Java
CertificateRepository repository = new PrefilledCertificateRepository("-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...");
Chain chain = DefaultChain.buildVerificationChain(repository);
// Scan the QR code from somewhere ...
String input = "HC1:NCFC:MVIMAP2SQ20MU...";

DecodeResult result = chain.decode(input);
VerificationResult verificationResult = result.getVerificationResult(); // contains metaInformation
boolean isValid = verificationResult.getError() == null; // true or false
Error error = verificationResult.getError(); // may be null, see list below
GreenCertificate greenCertificate = result.getChainDecodeResult().getEudgc(); // may be null
```

## Usage (JS)

The build result of this library for JS is a module in UMD format, under `build/distributions/hcert-kotlin.js`. This script runs in a web browser environment and can be used in the following way (see [demo.html](demo.html)).

Build the module either for development or production:
```
./gradlew jsBrowserDevelopmentWebpack
./gradlew jsBrowserProductionWebpack
```

To verify a single QR code content:

```JavaScript
let qr = "HC1:NCFC:MVIMAP2SQ20MU..."; // scan from somewhere
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq..."; // PEM encoded DSC
let verifier = new hcert.VerifierDirect([pemCert]); // would also accept more than one DSC

let result = verifier.verify(qr);
let isValid = result.isValid; // true or false
let metaInformation = result.metaInformation // see below
let error = result.error; // may be null, or contain an error, see below
let greenCertificate = result.greenCertificate; // may be null, or contain the decoded HCERT

console.info(result);
```

An alternative way of initializing the Verifier is by loading a TrustList, containing several DSC:

```JavaScript
let qr = "HC1:NCFC:MVIMAP2SQ20MU..."; // scan from somewhere
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq..."; // PEM encoded signer certificate from the TrustList
let trustListContent = new ArrayBuffer(8); // download it, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
let trustListSignature = new ArrayBuffer(8); // download it, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
let verifier = new hcert.VerifierTrustList(pemCert, trustListContent, trustListSignature);

let result = verifier.verify(qr);
// same as above ...
```

If you want to save the instance of `verifier` across several decodings, you can update the TrustList afterwards, too:

```JavaScript
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq..."; // PEM encoded signer certificate from the TrustList
let trustListContent = new ArrayBuffer(8); // download it, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
let trustListSignature = new ArrayBuffer(8); // download it, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
verifier.updateTrustList(pemCert, trustListContent, trustListSignature);

// go on with verifier.verify("...");
```

The meta information contains extracted data from the QR code contents, e.g.:

```JSON
{
  "expirationTime": "2021-11-02T18:00:00Z",
  "issuedAt": "2021-05-06T18:00:00Z",
  "issuer": "AT",
  "certificateValidFrom": "2021-05-05T12:41:06Z",
  "certificateValidUntil": "2023-05-05T12:41:06Z",
  "certificateValidContent": [
    "TEST",
    "VACCINATION",
    "RECOVERY"
  ],
  "content": [
    "VACCINATION"
  ],
  "error": null
}
```

Encoding of HCERT data, i.e. generating the input for an QR Code, as well as the QR Code picture:
```JavaScript
let generator = new hcert.GeneratorEcRandom(256); // creates a new EC key
let input = JSON.stringify({"ver": "1.2.1", "nam": { ... }}); // valid HCERT data

let result = generator.encode(input); // get a result with all intermediate steps

let qrCode = generator.encodeToQrCode(input, 4, 2); // 4 is the module size of the QR code, 2 is the margin size
// get a data URL of the encoded QR code picture, e.g. "data:image/gif;base64,AAA..."

console.info(result.step5Prefixed); // the contents of the QR code
```

You may also load a fixed key pair with certificate:
```JavaScript
let pemKey = "-----BEGIN PRIVATE KEY-----\nME0CAQAwE..."; // PEM encoded private key info, i.e. PKCS#8
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq..."; // PEM encoded signer certificate
let generator = new hcert.GeneratorFixed(pemKey, pemCert); // creates a new EC key
let input = JSON.stringify({"ver": "1.2.1", "nam": { ... }}); // valid HCERT data

let result = generator.encode(input); // get a result with all intermediate steps

let qrCode = generator.encodeToQrCode(input, 4, 2); // 4 is the module size of the QR code, 2 is the margin size
// get a data URL of the encoded QR code picture, e.g. "data:image/gif;base64,AAA..."

console.info(result.step5Prefixed); // the contents of the QR code
```

## Errors

The field `error` in the resulting structure (`DecodeResult`) may contain the error code. The list of possible errors is the same as for [ValidationCore](https://github.com/ehn-dcc-development/ValidationCore):
 - `GENERAL_ERROR`:
 - `INVALID_SCHEME_PREFIX`: The prefix does not conform to the expected one, e.g. `HC1:`
 - `DECOMPRESSION_FAILED`: Error in decompressing the input
 - `BASE_45_DECODING_FAILED`: Error in Base45 decoding
 - `COSE_DESERIALIZATION_FAILED`: not used
 - `CBOR_DESERIALIZATION_FAILED`: Error in decoding CBOR or CWT structures
 - `SCHEMA_VALIDATION_FAILED`: Data does not conform to schema (on iOS, this is a `CBOR_DESERIALIZATION_FAILED`)
 - `CWT_EXPIRED`: Timestamps in CWT are not correct, e.g. expired before issuing timestamp
 - `QR_CODE_ERROR`: not used
 - `CERTIFICATE_QUERY_FAILED`: not used
 - `USER_CANCELLED`: not used
 - `TRUST_SERVICE_ERROR`: General error when loading Trust List
 - `TRUST_LIST_EXPIRED`: Trust List has expired
 - `TRUST_LIST_NOT_YET_VALID`: Trust List is not yet valid
 - `TRUST_LIST_SIGNATURE_INVALID`: Signature on trust list is not valid
 - `KEY_NOT_IN_TRUST_LIST`: Certificate with `KID` not found
 - `PUBLIC_KEY_EXPIRED`: Certificate used to sign the COSE structure has expired
 - `UNSUITABLE_PUBLIC_KEY_TYPE`: Certificate has not the correct extension for signing that content type, e.g. Vaccination entries
 - `KEY_CREATION_ERROR`: not used
 - `KEYSTORE_ERROR`: not used
 - `SIGNATURE_INVALID`: Signature on COSE structure could not be verified

On JavaScript, the methods `updateTrustList` and `VerifierTrustList` may throw an error of the type `VerificationException` directly. The object has the following structure:
```JSON
{
  "message_8yp7un$_0": "Hash not matching",
  "cause_th0jdv$_0": null,
  "stack": "n/</e.captureStack@file:///...",
  "name": "VerificationException",
  "error": {
    "name$": "TRUST_LIST_SIGNATURE_INVALID",
    "ordinal$": 14
  }
}
```

The important bits are `name` (which should always be `VerificationException`) and `error.name$`, which contains the error code from the list above, e.g. `TRUST_LIST_SIGNATURE_INVALID`. See also <demo.html>.


## TrustList

There is also an option to create (on the service) and read (in the app) a list of trusted certificates for verification of HCERTs.

The server can create it:
```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
TrustListV2EncodeService trustListService = new TrustListV2EncodeService(cryptoService);

// Load the list of trusted certificates from somewhere ...
Set<X509Certificate> trustedCerts = new HashSet<>(cert1, cert2, ...);
byte[] encodedTrustList = trustListService.encodeContent(trustedCerts);
byte[] encodedTrustListSignature = trustListService.encodeSignature(encodedTrustList);
```

The client can use it for verification:
```Java
String trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CertificateRepository trustAnchor = new PrefilledCertificateRepository(trustListAnchor);
byte[] encodedTrustList = // file download etc
byte[] encodedTrustListSignature = // file download etc
CertificateRepository repository = new TrustListCertificateRepository(encodedTrustListSignature, encodedTrustList, trustAnchor);
Chain chain = DefaultChain.buildVerificationChain(repository);

// Continue as in the example above ...
```

## Data Classes

Sample data objects are provided in `SampleData`, with special thanks to Christian Baumann.

Classes in `ehn.techiop.hcert.kotlin.data` provide Kotlin data classes that conform to the JSON schema. They can be de-/serialized with [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization), i.e. `Cbor.encodeToByteArray()` or `Cbor.decodeFromByteArray<GreenCertificate>()`.

These classes also use `ValueSetEntry` objects, that are loaded from the valuesets of the dgc-schema. These provide additional information, e.g. for the key "EU/1/20/1528" to map to the vaccine "Comirnaty".

This implementation is on purpose lenient when parsing HCERT data, since there may be some production data out there, that includes timestamps in date objects, or whitespaces in keys for value sets.

## Configuration

Nearly every object in this library can be configured using constructor parameters. Most of these parameters have opinionated, default values, e.g. `Clock.System` for `clock`, used to get the current timestamp.

With the default configuration, schema validation of HCERT data is done against a very relaxed JSON schema, e.g. no `maxLength`, no `format`, no `pattern` for all fields. This is done to work around several faulty implementations of some countries, whose HCERT data would not be accepted by verifiers otherwise.

One example: The validity for the TrustList, as well as the validity of the HCERT in CBOR can be passed as a `validity` parameter (instance of a `Duration`) when constructing the objects:

```Java
CryptoService cryptoService = new RandomEcKeyCryptoService(256); // or some fixed key crypto service
HigherOrderValidationService higherOrdeValidationService = new DefaultHigherOrderValidationService();
SchemaValidationService schemaValidationService = new DefaultSchemaValidationService(); // pass "false" to disable fallback schema validation
CborService cborService = new DefaultCborService();
CwtService cwtService = new DefaultCwtService("AT", Duration.hours(24)); // validity for HCERT content
CoseService coseService = new DefaultCoseService(cryptoService);
CompressorService compressorService = new DefaultCompressorService(9); // level of compression
Base45Service base45Service = new DefaultBase45Service();
ContextIdentifierService contextIdentifierService = new DefaultContextIdentifierService("HC1:");


Chain chain = new Chain(higherOrderValidationService, schemaValidationService, cborService, cwtService, coseService, compressorService, base45Service, contextIdentifierService);
ChainResult result = chain.encode(input);
```

Implementers may load values for constructor parameters from a configuration file, e.g. with [Spring Boot's configuration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config).

## Publishing

To publish this package to GitHub, create a personal access token (read <https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages>), and add `gpr.user` and `gpr.key` in your `~/.gradle/gradle.properties` and run `./gradlew publish`

The library is also published on jitpack.io: [![](https://jitpack.io/v/ehn-dcc-development/hcert-kotlin.svg)](https://jitpack.io/#ehn-dcc-development/hcert-kotlin).

Use it in your project like this:

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ehn-dcc-development:hcert-kotlin:1.0.2-SNAPSHOT'
    implementation 'com.github.ehn-dcc-development:hcert-kotlin-jvm:1.0.2-SNAPSHOT'
}
```

If you are planning to use this library, we'll suggest to fork it (internally), and review incoming changes. We can not guarantee non-breaking changes between releases.

## Changelog

Version 1.0.1:
 - Validate schema on JVM
 - Fix usage of this project as a library, e.g. on Android

Version 1.0.0:
 - Convert to a Kotlin multiplatform project, therefore some details may have changed when calling from JVM
 - Implements encoding and decoding data on JS and JVM targets
 - Some testcases from `dgc-testdata` still fail

Version 0.4.0:
 - Update ehn-dgc-schema to 1.2.1
 - Include dgc-testdata as a git submodule

Version 0.3.1:
 - Implement a TrustList V2, with details is `hcert-service-kotlin`

Version 0.3.0:
 - Rename the previous `CborService` to `CwtService`, as the new name matches the implementation more closely
 - Introduce new `CborService` that just encodes HCERT as CBOR
 - Bugfix: Compression with ZLIB is in fact not optional when decoding QR codes
 - Bugfix: In CBOR, Dates need to be serialized as ISO 8601 compatible Strings, e.g. `2021-02-20T12:34:56Z`, not `1613824496000`

Version 0.2.2:
 - Changes to validity parameter for creating TrustList, HCERTs (`TrustListEncodeService` and `DefaultCborService`)
 - More options for creating 2D codes (`DefaultTwoDimCodeService`)
 - Implement first shot of reading standardized test cases in a JSON format (see `src/test/resources/testcase01.json`)
 - Use ValueSet instead of fixed enums for data in `GreenCertificate`
 - Update dgc-schema to version 1.0.0 from 2021-04-30

Version 0.2.1:
 - TrustList encodes public keys in PKCS#1 format (instead of PKCS#8/X.509)
 - Interface of `TwoDimCodeService` now returns a `ByteArray` instead of a `String`, callers need to encode the result to manually.

## Libraries

This library uses the following dependencies:
 - [Kotlin](https://github.com/JetBrains/kotlin), under the Apache-2.0 License
 - [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization), under the Apache-2.0 License
 - [Kotlinx Datetime](https://github.com/Kotlin/kotlinx-datetime), under the Apache-2.0 License
 - [Kotest](https://github.com/kotest/kotest), under the Apache-2.0 License

For the JVM target:
 - [COSE-JAVA](https://github.com/cose-wg/cose-java), under the BSD-3-Clause License
 - [ZXing](https://github.com/zxing/zxing), under the Apache-2.0 License
 - [Bouncycastle](https://github.com/bcgit/bc-java), under an [MIT-compatible license](https://www.bouncycastle.org/licence.html)
 - [JUnit](https://github.com/junit-team/junit5), under the Eclipse Public License v2.0
 - [json-kotlin-schema](https://github.com/pwall567/json-kotlin-schema), under the MIT License

For the JS target:
 - [pako](https://github.com/nodeca/pako), under the MIT License
 - [Types/Pako](https://github.com/DefinitelyTyped/DefinitelyTyped), under the MIT License
 - [PKIjs](https://github.com/PeculiarVentures/PKI.js), under a [custom license](https://github.com/PeculiarVentures/PKI.js/blob/master/LICENSE)
 - [cose-js](https://github.com/erdtman/COSE-JS), forked as a git submodule, under the Apache-2.0 License
 - [crypto-browserify](https://github.com/crypto-browserify/crypto-browserify), under the MIT License
 - [stream-browserify](https://github.com/browserify/stream-browserify), under the MIT License
 - [util](https://github.com/browserify/node-util), under the MIT License
 - [buffer](https://github.com/feross/buffer), under the MIT License
 - [process](https://github.com/defunctzombie/node-process), under the MIT License
 - [cbor](https://github.com/hildjj/node-cbor), under the MIT License
 - [node-inspect-extracted](https://github.com/hildjj/node-inspect-extracted), under an [MIT-compatible license](https://github.com/hildjj/node-inspect-extracted/blob/main/LICENSE)
 - [fast-sha256](https://github.com/dchest/fast-sha256-js), under the Unlicense
 - [url](https://github.com/defunctzombie/node-url), under the MIT License
 - [elliptic](https://github.com/indutny/elliptic), under the MIT License
 - [node-rsa](https://github.com/rzcoder/node-rsa), under the MIT License
 - [constants-browserify](https://github.com/juliangruber/constants-browserify), under the MIT License
 - [assert](https://github.com/browserify/commonjs-assert), under the MIT License
 - [base64url](https://github.com/brianloveswords/base64url), under the MIT License
 - [ajv](https://github.com/ajv-validator/ajv), under the MIT License
 - [ajv-formats](https://github.com/ajv-validator/ajv-formats), under the MIT License

Tip: Run `./gradlew generateLicenseReport`.
