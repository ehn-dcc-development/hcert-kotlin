# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain of electronic health certificates:
 - Encode in CBOR
 - Sign and embed in COSE
 - Compress with ZLib
 - Prepend with context identifier
 - Encode as QR Code

All services are implemented according to the [Specification 1.0.5](https://github.com/ehn-digital-green-development/hcert-spec), Version 1.0.5 from 2021-04-18.

The schemata for data classes is imported from <https://github.com/ehn-digital-green-development/ehn-dgc-schema>, from 2021-04-23.

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
ChainResult result = chain.process(input);

// Optionally encode it as a QR-Code with 350 pixel in width and height
TwoDimCodeService qrCodeService = new DefaultTwoDimCodeService(350);
String encodedBase64QrCode = qrCodeService.encode(result.step5Prefixed);

// Then include in an HTML page or something ...
String html = "<img src=\"data:image/png;base64,\" + encodedBase64QrCode + "\" />";
```

Example for the verification side, i.e. in apps:

```Java
CertificateRepository repository = PrefilledCertificateRepository(-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...");
Chain chain = Chain.buildVerificationChain();
// Scan the QR code from somewhere ...
String input = "HC1:NCFC:MVIMAP2SQ20MU...";

VerificationResult verificationResult = new VerificationResult();
Eudgc dgc = chain.decode(input, verificationResult);
VerificationDecision decision = new DecisionService().decide(verificationResult);
// is either VerificationDecision.GOOD or VerificationDecision.FAIL

// To convert the contents to data classes with meaningful property names:
GreenCertificate greenCertificate = Data.fromSchema(dgc);
```

There is also an option to create (on the service) and read (in the app) a list of trusted certificates for verification of HCERTs.

The server can create it:
```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
TrustListService trustListService = new TrustListService(cryptoService);

// Load the list of trusted certificates from somewhere ...
Set<X509Certificate> trustedCerts = new HashSet<>(cert1, cert2, ...);
byte[] encodedTrustList = trustListService.encode(trustedCerts);
```

The client can use it for verification:
```Java
String trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CertificateRepository trustAnchor = PrefilledCertificateRepository(trustListAnchor);
byte[] encodedTrustList = // file download etc
CertificateRepository repository = TrustListCertificateRepository(encodedTrustList, trustAnchor);
Chain chain = Chain.buildVerificationChain(repository);

// Continue as in the example above ...
VerificationResult verificationResult = new VerificationResult();
Eudgc dgc = chain.decode(input, verificationResult);
```

## Data Classes

The JSON schema is copied to `src/main/resources/json`. From there, the Gradle plugin `org.jsonschema2dataclass` will create Java classes into the package `ehn.techiop.hcert.data`. The Gradle task `compileKotlin` depends on `generateJsonSchema2DataClass`, so that fresh classes are used for each compilation. The data classes can be de-/serialized with the Jackson library.

Sample data objects are provided in `SampleData`, with special thanks to Christian Baumann.

Classes in `ehn.techiop.hcert.kotlin.data` provide more meaningful names for data deserialized from an HCERT structure. It can be converted using `GreenCertificate.fromEuSchema(eudgcObject)`.

## TODO

- KID of certs can collide, be aware of that
- Implement decoding and verifying OID for specifying allowed signature

## Publishing

To publish this package to GitHub, create a personal access token (read <https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages>), and add `gpr.user` and `gpr.key` in your `~/.gradle/gradle.properties` and run `./gradlew publish`

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
