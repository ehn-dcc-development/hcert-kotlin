# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain of electronic health certificates:
 - Encode in CBOR
 - Wrap in a CWT structure
 - Sign and embed in COSE
 - Compress with ZLib
 - Encode in Base45
 - Prepend with Context Identifier
 - Encode as QR Code

All services are implemented according to the [Specification 1.0.5](https://github.com/ehn-digital-green-development/hcert-spec), Version 1.0.5 from 2021-04-18.

The schemata for data classes is imported from <https://github.com/ehn-digital-green-development/ehn-dgc-schema>, Version 1.0.0, from 2021-04-30.

The test resources are imported as a git submodule from <https://github.com/eu-digital-green-certificates/dgc-testdata/> into `src/commonTest/resources/dgc-testdata`. Please clone this repository with `git clone --recursive` or run `git submodule init && git submodule update` afterwards.

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

## TrustList

There is also an option to create (on the service) and read (in the app) a list of trusted certificates for verification of HCERTs.

The server can create it:
```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
TrustListEncodeService trustListService = new TrustListEncodeService(cryptoService);

// Load the list of trusted certificates from somewhere ...
Set<X509Certificate> trustedCerts = new HashSet<>(cert1, cert2, ...);
byte[] encodedTrustList = trustListService.encode(trustedCerts);
```

The client can use it for verification:
```Java
String trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CertificateRepository trustAnchor = new PrefilledCertificateRepository(trustListAnchor);
byte[] encodedTrustList = // file download etc
CertificateRepository repository = new TrustListCertificateRepository(encodedTrustList, trustAnchor);
Chain chain = Chain.buildVerificationChain(repository);

// Continue as in the example above ...
VerificationResult verificationResult = new VerificationResult();
Eudgc dgc = chain.decode(input, verificationResult);
```

The contents of the TrustList are CBOR encoded and wrapped in a COSE structure, similar to HCERT. We can define the schema loosely in this way:
```
TRUSTLIST := {
  f := validFrom of the list as UNIX timestamp (seconds since epoch),
  u := validUntil of the list as UNIX timestamp (seconds since epoch),
  c := [{
    f := validFrom of the certificate as UNIX timestamp (seconds since epoch),
    u := validUntil of the certificate as UNIX timestamp (seconds since epoch),
    i := the KID as a bytes,
    k := the key type, either "e" for EC, or "r" for RSA
    p := the bytes of the public key, either in X9.63 format
         for EC, that is X9.63 format: "04 || X || Y",
         for RSA, that is PKCS#1 format: "ASN1-SEQUENCE { MODULUS, EXPONENT }"
    t := the list of valid content types (i.e. mapped OIDs), where
         "t" is for test statements,
         "v" for vaccination statements,
         "r" for recovery statements
  }]
}
```

An (non-normative) example is:

```
d28450a3182a0104485384d564f888ad380126a05901bebf61661a608c036361751a608ea6636163
9fbf61661a608c036261751a60b39062616948e8b7a2ef7c2f7145616b61656170584104637a400e
ad4a003d48bc8a9b24bc289d9dc3d3702cf3fc962d23550523820e74202e3efb1098d6da05a34e46
c0f2a27925aeb43d4d4e4daa8d62b99ad4d1eee561749f617461766172ffffbf61661a608c036361
751a60b390636169482145b0ff85107236616b6172617059010e3082010a02820101009104155ef5
9a146c33ddca77192036973584855fe92ac86829e3c2524bd76c8e59c02fd1eaf0955e920cbb0643
67a6374f271073ac44cf4382be0a87c3bcd001406710878d4641c73e7a7f125c571886a707d1a6bc
523bd4c9582b8271a2b1d8cfd2d7ea04f14edbfebe899173134987d741d3e21236f18acbe4cac0ef
7d8000734cfba6e4b8f55af7d8bd16fd64ec9eaa89006ce9d5edfb3d907858625df1d9eaeabf095c
bdd4b7f27a50fbacfe4c3ee7c029b364b23cb0b5a88665096f30353116bef1c4565ed4c32bf5109d
915a6d34a4405f08b2eb2ca797881f88075623208033a44c4b414a4494508da8e0943a23519eabb2
5340610e9a236c3d16a65f020301000161749f617461766172ffffffff5840165653cc24535330fc
fa1c53591225f594240d684f76f3bfbbb608c2007f5cb63d3f184b9e1fa50fb45e14f0ba5060688c
e6c15cfa4e29cbefe3d5326c4f558f
```

decoded to the following COSE structure:

```
18([
  h'A3182A0104485384D564F888AD380126',
  {},
  h'BF61661A608C036361751A608EA66361639FBF61661A608C036261751A60B39062616948E8B7
    A2EF7C2F7145616B61656170584104637A400EAD4A003D48BC8A9B24BC289D9DC3D3702CF3FC
    962D23550523820E74202E3EFB1098D6DA05A34E46C0F2A27925AEB43D4D4E4DAA8D62B99AD4
    D1EEE561749F617461766172FFFFBF61661A608C036361751A60B390636169482145B0FF8510
    7236616B6172617059010E3082010A02820101009104155EF59A146C33DDCA77192036973584
    855FE92AC86829E3C2524BD76C8E59C02FD1EAF0955E920CBB064367A6374F271073AC44CF43
    82BE0A87C3BCD001406710878D4641C73E7A7F125C571886A707D1A6BC523BD4C9582B8271A2
    B1D8CFD2D7EA04F14EDBFEBE899173134987D741D3E21236F18ACBE4CAC0EF7D8000734CFBA6
    E4B8F55AF7D8BD16FD64EC9EAA89006CE9D5EDFB3D907858625DF1D9EAEABF095CBDD4B7F27A
    50FBACFE4C3EE7C029B364B23CB0B5A88665096F30353116BEF1C4565ED4C32BF5109D915A6D
    34A4405F08B2EB2CA797881F88075623208033A44C4B414A4494508DA8E0943A23519EABB253
    40610E9A236C3D16A65F020301000161749F617461766172FFFFFFFF',
  h'165653CC24535330FCFA1C53591225F594240D684F76F3BFBBB608C2007F5CB63D3F184B9E1F
    A50FB45E14F0BA5060688CE6C15CFA4E29CBEFE3D5326C4F558F'
])
```

with this protected header:

```
{
  1: -7,                  // the key type of the signing certificate, i.e. EC
  4: h'5384D564F888AD38', // the KID of the signing certificate
  42: 1                   // the version number of the format
}
```

with this CBOR content:

```
{
  "f": 1619788643,
  "u": 1619961443,
  "c": [{
      "f": 1619788642,
      "u": 1622380642,
      "i": h'E8B7A2EF7C2F7145',
      "k": "e",
      "p": h'04637A400EAD4A003D48BC8A9B24BC289D9DC3D3702CF3FC962D23550523820E742
             02E3EFB1098D6DA05A34E46C0F2A27925AEB43D4D4E4DAA8D62B99AD4D1EEE5',
      "t": ["t", "v", "r"]
    }, {
      "f": 1619788643,
      "u": 1622380643,
      "i": h'2145B0FF85107236',
      "k": "r",
      "p": h'3082010A02820101009104155EF59A146C33DDCA77192036973584855FE92AC8682
             9E3C2524BD76C8E59C02FD1EAF0955E920CBB064367A6374F271073AC44CF4382BE
             0A87C3BCD001406710878D4641C73E7A7F125C571886A707D1A6BC523BD4C9582B8
             271A2B1D8CFD2D7EA04F14EDBFEBE899173134987D741D3E21236F18ACBE4CAC0EF
             7D8000734CFBA6E4B8F55AF7D8BD16FD64EC9EAA89006CE9D5EDFB3D907858625DF
             1D9EAEABF095CBDD4B7F27A50FBACFE4C3EE7C029B364B23CB0B5A88665096F3035
             3116BEF1C4565ED4C32BF5109D915A6D34A4405F08B2EB2CA797881F88075623208
             033A44C4B414A4494508DA8E0943A23519EABB25340610E9A236C3D16A65F020301
             0001',
      "t": ["t", "v", "r"]
    }]
}
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
