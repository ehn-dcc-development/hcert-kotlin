# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain of electronic health certificates:
 - Encode in CBOR
 - Sign and embed in COSE
 - Compress with ZLib
 - Prepend with context identifier
 - Encode as QR Code

All services are implemented according to the [Specification 1.0.5](https://github.com/ehn-digital-green-development/hcert-spec), Version 1.0.5 from 2021-04-18.

The schemata for data classes is imported from <https://github.com/ehn-digital-green-development/ehn-dgc-schema>, Version 1.0.0, from 2021-04-30.

## Usage

`ehn.techiop.hcert.kotlin.chain.Chain` is the main class for encoding and decoding HCERT data. For encoding, pass an instance of a `Eudgc` (class generated from the JSON schema) and get a `ChainResult`. That object will contain all revelant intermediate results as well as the final result (`step5Prefixed`). This final result can be passed to a `DefaultTwoDimCodeService` that will encode it as a 2D QR Code.

The usage of interfaces for all services (CBOR, COSE, ZLib, Context) in the chain may seem over-engineered at first, but it allows us to create wrongly encoded results, by passing faulty implementations of the service. Those services reside in the namespace `ehn.techiop.hcert.kotlin.chain.faults` and should, obviously, not be used for production code.

The actual, correct, implementations of the service interfaces reside in `ehn.techiop.hcert.kotlin.chain.impl`. These "default" implementations will be used when the chain is constructed using `Chain.buildCreationChain()` or `Chain.buildVerificationChain()`.


Example for creation services:

```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
Chain chain = Chain.buildCreationChain(cryptoService);

// Load the input data from somewhere ...
String json = "{ \"sub\": { \"gn\": \"Gabriele\", ...";
String input = new ObjectMapper().readValue(jsonInput, Eudgc.class);

// Apply all encoding steps from the Chain: CBOR, COSE, ZLIB, Base45, Context
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
Chain chain = Chain.buildVerificationChain();
// Scan the QR code from somewhere ...
String input = "HC1:NCFC:MVIMAP2SQ20MU...";

VerificationResult verificationResult = new VerificationResult();
Eudgc dgc = chain.decode(input, verificationResult);
VerificationDecision decision = new DecisionService().decide(verificationResult);
// is either VerificationDecision.GOOD or VerificationDecision.FAIL

// To convert the contents to data classes with meaningful property names:
GreenCertificate greenCertificate = GreenCertificate.fromEuSchema(dgc);
```

## Data Classes

The JSON schema is copied to `src/main/resources/json`. From there, the Gradle plugin `org.jsonschema2dataclass` will create Java classes into the package `ehn.techiop.hcert.data`. The Gradle task `compileKotlin` depends on `generateJsonSchema2DataClass`, so that fresh classes are used for each compilation. The data classes can be de-/serialized with the Jackson library, e.g. using `CBORMapper` or `ObjectMapper`.

Sample data objects are provided in `SampleData`, with special thanks to Christian Baumann.

Classes in `ehn.techiop.hcert.kotlin.data` provide more meaningful names for data deserialized from an HCERT structure. It can be converted using `GreenCertificate.fromEuSchema(eudgcObject)`. Those classes can also be de-/serialized with [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization), i.e. `Cbor.encodeToByteArray()` or `Cbor.decodeFromByteArray<GreenCertificate>()`.

These classes also use `ValueSetEntry` objects, that are loaded from the valuesets of the dgc-schema. These provide additional information, e.g. for the key "EU/1/20/1528" to map to the vaccine "Comirnaty".

## Configuration

Nearly every object in this library can be configured using constructor parameters. Most of these parameters have, opinionated, default values, e.g. `Clock.systemUTC()` for `clock`, used to get the current timestamp.

One example: The validity for the TrustList, as well as the validity of the HCERT in CBOR can be passed as a `validity` parameter (instance of a `Duration`) when constructing the objects:

```Java
CryptoService cryptoService = new RandomEcKeyCryptoService(256); // or some fixed key crypto service
CborService cborService = new DefaultCborService();
CwtService cwtService = new DefaultCwtService("AT", Duration.ofHours(24)); // validity for HCERT content
CoseService coseService = new DefaultCoseService(cryptoService);
ContextIdentifierService contextIdentifierService = new DefaultContextIdentifierService("HC1:");
CompressorService compressorService = new DefaultCompressorService(9); // level of compression
Base45Service base45Service = new DefaultBase45Service();

Chain chain = new Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service);
ChainResult result = chain.encode(input);
```

Implementers may load values for constructor parameters from a configuration file, e.g. with [Spring Boot's configuration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config).

## Publishing

To publish this package to GitHub, create a personal access token (read <https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages>), and add `gpr.user` and `gpr.key` in your `~/.gradle/gradle.properties` and run `./gradlew publish`

## Changelog

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
 - [COSE-JAVA](https://github.com/cose-wg/cose-java), under the BSD-3-Clause License
 - [Jackson](https://github.com/FasterXML/jackson-databind), under the Apache-2.0 License
 - [ZXing](https://github.com/zxing/zxing), under the Apache-2.0 License
 - [Bouncycastle](https://github.com/bcgit/bc-java), under an [MIT-compatible license](https://www.bouncycastle.org/licence.html)
 - [JUnit](https://github.com/junit-team/junit5), under the Eclipse Public License v2.0
 - [Hamcrest](https://github.com/hamcrest/JavaHamcrest), under the BSD-3-Clause License
