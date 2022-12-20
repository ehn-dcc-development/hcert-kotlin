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

[![](https://jitpack.io/v/ehn-dcc-development/hcert-kotlin.svg)](https://jitpack.io/#ehn-dcc-development/hcert-kotlin)

## Usage (JVM)

The main class for encoding and decoding HCERT data is `ehn.techiop.hcert.kotlin.chain.Chain`. For encoding, pass an instance of a `GreenCertificate` (data class conforming to the DCC schema) and get a `ChainResult`. That object will contain all revelant intermediate results as well as the final result (`step5Prefixed`). This final result can be passed to a `DefaultTwoDimCodeService` that will encode it as a 2D QR Code.

Correct implementations of the service interfaces reside in `ehn.techiop.hcert.kotlin.chain.impl`. These "default" implementations will be used when the chain is constructed using `DefaultChain.buildCreationChain()` or `DefaultChain.buildVerificationChain()`.

Example for creation services:

```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
Chain chain = DefaultChain.buildCreationChain(cryptoService); //optional custom prefix, e.g. "AT1:" to support AT-specific exemption certificates

// Load the input data from somewhere ...
String json = "{ \"sub\": { \"gn\": \"Gabriele\", ...";
GreenCertificate input = Json.Default.decodeFromString(GreenCertificate.Companion.serializer(), json);

// Apply all encoding steps from the Chain
ChainResult result = chain.encode(input);

// Optionally encode it as a QR-Code with 350 pixel in width and height
TwoDimCodeService qrCodeService = new DefaultTwoDimCodeService(350);
byte[] encodedImage = qrCodeService.encode(result.getStep5Prefixed());
String encodedBase64QrCode = Base64.getEncoder().encodeToString(encodedImage);

// Then include in an HTML page or something ...
String html = "<img src=\"data:image/png;base64," + encodedBase64QrCode + "\" />";
```

Example for the verification side, i.e. in apps:

```Java
// Load the certificate from somewhere ...
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CertificateRepository repository = new PrefilledCertificateRepository(certificatePem);
Chain chain = DefaultChain.buildVerificationChain(repository);  //optional parameter atRepository to verify vaccination exemptions (prefix AT1:) against

// Scan the QR code from somewhere ...
String input = "HC1:NCFC:MVIMAP2SQ20MU...";

DecodeResult result = chain.decode(input);
// Read metaInformation like expirationTime, issuedAt, issuer
VerificationResult verificationResult = result.getVerificationResult();
boolean isValid = verificationResult.getError() == null;
// See list below for possible Errors, may be null
Error error = verificationResult.getError();
// Result data may be null
GreenCertificate greenCertificate = result.getChainDecodeResult().getEudgc();
```

You may also load a trust list from a server, that contains several trusted certificates:
```Java
// PEM-encoded signer certificate of the trust list
String trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CertificateRepository trustAnchor = new PrefilledCertificateRepository(trustListAnchor);
// Download trust list content, binary, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
byte[] trustListContent = new byte[0];
// Download trust list signature, binary, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
byte[] trustListSignature = new byte[0];
SignedData trustList = new SignedData(trustListContent, trustListSignature);
CertificateRepository repository = new TrustListCertificateRepository(trustList, trustAnchor);
Chain chain = DefaultChain.buildVerificationChain(repository);

// Continue as in the example above ..
```

### Faulty Implementations

The usage of interfaces for all services (CBOR, CWT, COSE, ZLib, Context) in the chain may seem over-engineered at first, but it allows us to create wrongly encoded results, by passing faulty implementations of the service. Those services reside in a separate artifact named `ehn.techiop.hcert:hcert-kotlin-jvmdatagen` in the namespace `ehn.techiop.hcert.kotlin.chain.faults` and should, obviously, not be used for production code.

Sample data objects are provided in `SampleData`, with special thanks to Christian Baumann.

### Debug Chain
In addition to the default (spec-compliant) verification behaviour, it is possible to continue verification even after certain errors.
While a faulty encoding or garbled CBOR structure will still result in fatal errors, an expired certificate, or unknown KID, will not terminate the verification procedure, when using the debug chain.
For details, see [`DebugChain.kt`](src/commonMain/kotlin/ehn/techiop/hcert/kotlin/chain/debug/DebugChain.kt).

### Anyonymising Personal Data (JVM only)
Both the `ChainDecodeResult` and the `GreenCertificate` classes allow for blanking personal information (name, date of birth), through the lazy-initialised `anonymizedCopy` property.
For debugging purposes, the `DecodeResult` features a `toJsonString(anonymize: Boolean)` method.

**NOTE:** This is blanking of personal data is limited to humanly comprehensible representations of processed data.
As such, even anonymised `DecodeResults` and `ChainDecodeResults` will contain unaltered QR code content, the vanilla CWT and so forth.
All such unmodified data can thus be parsed without issue and will still yield all personal data.
<hr>

**DO LOG OR PROCESS THIS DATA, EVEN WHEN USING ANONYMISED COPIES! YOU HAVE BEEN WARNED.**

<hr>

## Usage (JS)

The build result of this library for JS is a module in UMD format, located under `build/distributions/hcert-kotlin.js`. This script runs in a web browser environment and can be used in the following way (see [demo.html](demo.html)).
In addition, we also (experimentally) support node as target environment (also based on a bundled UMD) by passing the `node` flag to gradle (see the [sample node project](node-demo)).

Build the module either for development or production for a **browser** target:
```
./gradlew jsBrowserDevelopmentWebpack
./gradlew jsBrowserProductionWebpack
```

Build the module either for development or production (**NodeJS** target):
```
./gradlew -Pnode jsBrowserDevelopmentWebpack
./gradlew -Pnode jsBrowserProductionWebpack
```


To verify a single QR code content:

```JavaScript
// PEM-encoded DSC
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Would also accept more than one DSC
let verifier = new hcert.VerifierDirect([pemCert]); //optional second parameter: array of pem encoded certs to verify vaccination exemptions (prefix AT1:) against

// Scan the QR code from somewhere ...
let qr = "HC1:NCFC:MVIMAP2SQ20MU...";
let result = verifier.verify(qr);

let isValid = result.isValid;
// Read metaInformation like expirationTime, issuedAt, issuer
let metaInformation = result.metaInformation;
// See list below for possible Errors, may be null
let error = result.error;
// Result data may be null, contains decoded HCERT
let greenCertificate = result.greenCertificate;
```

An alternative way of initializing the `Verifier` is by loading a trust list:

```JavaScript
// PEM-encoded signer certificate of the trust list
let trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Download trust list content, binary, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
let trustListContent = new ArrayBuffer(8);
// Download trust list signature, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
let trustListSignature = new ArrayBuffer(8);

let verifier = new hcert.VerifierTrustList(trustListAnchor, trustListContent, trustListSignature);  //optional isAT flag as fourth parameter to
                                                                                                    //update AT-specific trust ancors to verify
                                                                                                    //vaccination exemptions (prefix AT1:) against
// Continue with example above with verifier.verify()
```

If you want to save the instance of `verifier` across several decodings, you can update the TrustList afterwards, too:

```JavaScript
// PEM-encoded signer certificate of the trust list
let trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Download trust list content, binary, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
let trustListContent = new ArrayBuffer(8);
// Download trust list signature, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
let trustListSignature = new ArrayBuffer(8);
verifier.updateTrustList(trustListAnchor, trustListContent, trustListSignature);

// Continue with example above with verifier.verify();
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
  "certificateSubjectCountry": "AT",
  "content": [
    "VACCINATION"
  ],
  "error": null
}
```

Encoding of HCERT data, i.e. generating the input for an QR Code, as well as the QR Code picture:
```JavaScript
// Create a new, random EC key with 256 bits key size, i.e. on P-256
let generator = new hcert.GeneratorEcRandom(256);
// Provide valid HCERT data
let input = JSON.stringify({"ver": "1.2.1", "nam": { ... }});

// Get a result with all intermediate steps
let result = generator.encode(input);

// Print the contents of the QR code
console.info(result.step5Prefixed);

// Alternative: Get a data URL of the encoded QR code picture, e.g. "data:image/gif;base64,AAA..."
let moduleSize = 4;
let marginSize = 2;
let qrCode = generator.encodeToQrCode(input, moduleSize, marginSize);
```

You may also load a fixed key pair with certificate:
```JavaScript
// PEM-encoded private key info, i.e. PKCS#8
let pemKey = "-----BEGIN PRIVATE KEY-----\nME0CAQAwE...";
// PEM-encoded certificate
let pemCert = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Load the private key and certificate
let generator = new hcert.GeneratorFixed(pemKey, pemCert);
// Provide valid HCERT data
let input = JSON.stringify({"ver": "1.2.1", "nam": { ... }});

// Continue with example above with generator.encode()
```

An alternative to calling `verfiy(qr)` is to call `verifyDataClass(qr)` which returns the `greenCertificate` as an JS object, like this:

```JSON
{
  "schemaVersion": "1.0.0",
  "subject": {
    "familyName": "Musterfrau-Gößinger",
    "familyNameTransliterated": "MUSTERFRAU<GOESSINGER",
    "givenName": "Gabriele",
    "givenNameTransliterated": "GABRIELE"
  },
  "dateOfBirthString": "1998-02-26",
  "vaccinations": [
    {
      "target": {
        "key": "840539006",
        "valueSetEntry": {
          "display": "COVID-19",
          "lang": "en",
          "active": true,
          "system": "http://snomed.info/sct",
          "version": "http://snomed.info/sct/900000000000207008/version/20210131",
          "valueSetId": null
        }
      },
      "vaccine": {
        "key": "1119305005",
        "valueSetEntry": {
          "display": "SARS-CoV-2 antigen vaccine",
          "lang": "en",
          "active": true,
          "system": "http://snomed.info/sct",
          "version": "http://snomed.info/sct/900000000000207008/version/20210131",
          "valueSetId": null
        }
      },
      "medicinalProduct": {
        "key": "EU/1/20/1528",
        "valueSetEntry": {
          "display": "Comirnaty",
          "lang": "en",
          "active": true,
          "system": "https://ec.europa.eu/health/documents/community-register/html/",
          "version": "",
          "valueSetId": null
        }
      },
      "authorizationHolder": {
        "key": "ORG-100030215",
        "valueSetEntry": {
          "display": "Biontech Manufacturing GmbH",
          "lang": "en",
          "active": true,
          "system": "https://spor.ema.europa.eu/v1/organisations",
          "version": "",
          "valueSetId": "vaccines-covid-19-auth-holders"
        }
      },
      "doseNumber": 1,
      "doseTotalNumber": 2,
      "date": "2021-02-18T00:00:00.000Z",
      "country": "AT",
      "certificateIssuer": "BMSGPK Austria",
      "certificateIdentifier": "urn:uvci:01:AT:10807843F94AEE0EE5093FBC254BD813P"
    }
  ],
  "recoveryStatements": null,
  "tests": null,
  "dateOfBirth": "1998-02-26T00:00:00.000Z"
}
```

### Debug Chain
In addition to the default (spec-compliant) verification behaviour, it is possible to continue verification even after certain errors.
While a faulty encoding or garbled CBOR structure will still result in fatal errors, an expired certificate, or unknown KID, will not terminate the verification procedure, when using the debug chain.
Simply add a `true` as the additional parameter to verifier  constructor calls, such as `new hcert.VerifierDirect([pemCert], true)`.




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
 - `CWT_NOT_YET_VALID`: Timestamps in CWT are not correct, e.g. issued after the current time
 - `QR_CODE_ERROR`: not used
 - `CERTIFICATE_QUERY_FAILED`: not used
 - `USER_CANCELLED`: not used
 - `TRUST_SERVICE_ERROR`: General error when loading Trust List or Business Rules
 - `TRUST_LIST_EXPIRED`: Trust List (or Business Rules) has expired
 - `TRUST_LIST_NOT_YET_VALID`: Trust List (or Business Rules) is not yet valid
 - `TRUST_LIST_SIGNATURE_INVALID`: Signature on Trust List (or Business Rules) is not valid
 - `KEY_NOT_IN_TRUST_LIST`: Certificate with `KID` not found
 - `PUBLIC_KEY_EXPIRED`: Certificate used to sign the COSE structure has expired
 - `PUBLIC_KEY_NOT_YET_VALID`: Certificate used to sign the COSE structure is not yet valid
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

There is also an option to create (e.g. on a web service) a list of trusted certificates for verification of HCERTs:
```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
int validityHours = 48;
TrustListV2EncodeService trustListService = new TrustListV2EncodeService(cryptoService, validityHours);

// Load the list of trusted certificates from somewhere ...
Set<CertificateAdapter> trustedCerts = new HashSet<>(cert1, cert2, ...);
SignedData trustList = trustListService.encode(trustedCerts);
// Write content file
new FileOutputStream(new File("trustlist.bin")).write(trustList.getContent());
// Write signature file
new FileOutputStream(new File("trustlist.sig")).write(trustList.getSignature());
```

Clients may load these files to get the Trusted Certificates plus meta information:

```Java
// PEM-encoded signer certificate of the trustList
CertificateRepository trustAnchor = new PrefilledCertificateRepository("-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...");
// Download trust list content, binary, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
byte[] trustListContent = new byte[0];
// Download trust list signature, binary, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
byte[] trustListSignature = new byte[0];
SignedData trustList = new SignedData(trustListContent, trustListSignature);

TrustListDecodeService service = new TrustListDecodeService(trustAnchor);
Pair<SignedDataParsed, TrustListV2> result = service.decode(trustList);
// Contains "validFrom", "validUntil"
SignedDataParsed parsed = result.getFirst();
// Contains a list of certificates in X.509 encoding
TrustListV2 trustListContainer = result.getSecond();
for (TrustedCertificateV2 cert : trustListContainer.getCertificates()) {
    // Parse it into your own data class
    System.out.println(ExtensionsKt.asBase64(cert.getCertificate()));
}
```

or in JavaScript:

```JavaScript
// PEM-encoded signer certificate of the trust list
let trustListAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Download trust list content, binary, e.g. from https://dgc.a-sit.at/ehn/cert/listv2
let trustListContent = new ArrayBuffer(8);
// Download trust list signature, binary,, e.g. from https://dgc.a-sit.at/ehn/cert/sigv2
let trustListSignature = new ArrayBuffer(8);

let result = hcert.SignedDataDownloader.loadTrustList(trustListAnchor, trustListContent, trustListSignature);
// Contains "validFrom" and "validUntil" as JS Dates
console.log(result.first);
// Contains an array of "certificates", each with "kid" and "certificate" as Int8Array
console.log(result.second);
```

## Business Rules

There is also an option to create (e.g. on a web service) a list of business rules, that may be used to further verify HCERTs:

```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
int validityHours = 48;
BusinessRulesV1EncodeService rulesService = new BusinessRulesV1EncodeService(cryptoService, validityHours);

// Load the list business rules
List<String> inputStrings = new ArrayList<>();
List<BusinessRule> input = inputStrings.stream().map(it -> new BusinessRule("identifier", it)).collect(Collectors.toList());
SignedData rules = rulesService.encode(input);
// Write content file
new FileOutputStream(new File("rules.bin")).write(rules.getContent());
// Write signature file
new FileOutputStream(new File("rules.sig")).write(rules.getSignature());
```

Clients may load these files to get a list of trusted Business Rules plus meta information:

```Java
// PEM-encoded signer certificate of the rules
CertificateRepository trustAnchor = new PrefilledCertificateRepository("-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...");
// Download rules content, binary, e.g. from https://dgc.a-sit.at/ehn/rules/v1/bin
byte[] rulesContent = new byte[0];
// Download rules signature, binary, e.g. from https://dgc.a-sit.at/ehn/rules/v1/sig
byte[] rulesSignature = new byte[0];
SignedData rulesSigned = new SignedData(rulesContent, rulesSignature);

BusinessRulesDecodeService service = new BusinessRulesDecodeService(trustAnchor);
Pair<SignedDataParsed, BusinessRulesContainer> result = service.decode(rulesSigned);
// Contains "validFrom", "validUntil"
SignedDataParsed parsed = result.getFirst();
// Contains a list of business rules as raw JSON
BusinessRulesContainer rules = result.getSecond();
for (BusinessRule rule : rules.getRules()) {
    // Parse it into your own data class
    System.out.println(rule.getRule());
}
```

or in JavaScript:

```JavaScript
// PEM-encoded signer certificate of the rules
let rulesAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Download rules content, binary, e.g. from https://dgc.a-sit.at/ehn/rules/v1/bin
let rulesContent = new ArrayBuffer(8);
// Download rules signature, binary,, e.g. from https://dgc.a-sit.at/ehn/rules/v1/sig
let rulesSignature = new ArrayBuffer(8);

let result = hcert.SignedDataDownloader.loadBusinessRules(rulesAnchor, rulesContent, rulesSignature);
// Contains "validFrom" and "validUntil" as JS Dates
console.log(result.first);
// Contains an array of "rules", each with a "identifier" and "rule" (raw JSON string)
console.log(result.second);
```

## Value Sets

There is also an option to create (e.g. on a web service) a list of value sets, that may be used to enrich data in HCERTs:

```Java
// Load the private key and certificate from somewhere ...
String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADAN...";
String certificatePem = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
CryptoService cryptoService = new FileBasedCryptoService(privateKeyPem, certificatePem);
        int validityHours = 48;
ValueSetV1EncodeService valueSetService = new ValueSetV1EncodeService(cryptoService, validityHours);

// Load the list value sets
List<String> inputStrings = new ArrayList<>();
List<ValueSet> input = inputStrings.stream().map(it -> new ValueSet(it)).collect(Collectors.toList());
SignedData valueSet = valueSetService.encode(input);
// Write content file
new FileOutputStream(new File("valueSet.bin")).write(valueSet.getContent());
// Write signature file
new FileOutputStream(new File("valueSet.sig")).write(valueSet.getSignature());
```

Clients may load these files to get a list of trusted Value Sets plus meta information:

```Java
// PEM-encoded signer certificate of the valueSet
CertificateRepository trustAnchor = new PrefilledCertificateRepository("-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...");
// Download valueSet content, binary, e.g. from https://dgc.a-sit.at/ehn/values/v1/bin
byte[] valueSetContent = new byte[0];
// Download valueSet signature, binary, e.g. from https://dgc.a-sit.at/ehn/values/v1/sig
byte[] valueSetSignature = new byte[0];
SignedData valueSetSigned = new SignedData(valueSetContent, valueSetSignature);

ValueSetDecodeService service = new ValueSetDecodeService(trustAnchor);
Pair<SignedDataParsed, ValueSetContainer> result = service.decode(valueSetSigned);
// Contains "validFrom", "validUntil"
SignedDataParsed parsed = result.getFirst();
// Contains a list of value sets as raw JSON
ValueSetContainer valueSet = result.getSecond();
for (ValueSet vs : valueSet.getValueSets()) {
    // Parse it into your own data class
    System.out.println(vs.getValueSet());
}
```

or in JavaScript:

```JavaScript
// PEM-encoded signer certificate of the rules
let vaulesAnchor = "-----BEGIN CERTIFICATE-----\nMIICsjCCAZq...";
// Download values content, binary, e.g. from https://dgc.a-sit.at/ehn/values/v1/bin
let valuesContent = new ArrayBuffer(8);
// Download values signature, binary,, e.g. from https://dgc.a-sit.at/ehn/values/v1/sig
let valuesSignature = new ArrayBuffer(8);

let result = hcert.SignedDataDownloader.loadBusinessValues(valuesAnchor, valuesContent, valuesSignature);
// Contains "validFrom" and "validUntil" as JS Dates
console.log(result.first);
// Contains an array of "valueSets", each with a "name" and "valueSet" (raw JSON string)
console.log(result.second);
```

## Data Classes

Classes in `ehn.techiop.hcert.kotlin.data` provide Kotlin data classes that conform to the JSON schema. They can be de-/serialized with [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization), i.e. `Cbor.encodeToByteArray()` or `Cbor.decodeFromByteArray<GreenCertificate>()`.

These classes also use `ValueSetEntry` objects, that are loaded from the valuesets of the dgc-schema. These provide additional information, e.g. for the key "EU/1/20/1528" to map to the vaccine "Comirnaty".

This implementation is on purpose lenient when parsing HCERT data, since there may be some production data out there, that includes timestamps in date objects, or whitespaces in keys for value sets.

For JS, you can call `verifyDataClass(qr)` (istead of `verify(qr)`) to get an instance of a monkey-patched `GreenCertificate`. This class is essentially the same as `GreenCertificate` for the JVM target, but holds JS `Date` objects instead of the JVM types for dates and instants. In contrast to the simple call to `verify(qr)`, you'll get a `valueSetEntry` (if one is found) and descriptive property names.

## Configuration

Nearly every object in this library can be configured using constructor parameters. Most of these parameters have opinionated, default values, e.g. `Clock.System` for `clock`, used to get the current timestamp.

With the default configuration, schema validation of HCERT data is done against a very relaxed JSON schema, e.g. no `maxLength`, no `format`, no `pattern` for all fields. This is done to work around several faulty implementations of some countries, whose HCERT data would not be accepted by verifiers otherwise.

One example: The validity for the TrustList, as well as the validity of the HCERT in CBOR can be passed as a `validity` parameter (instance of a `Duration`) when constructing the objects:

```Java
CryptoService cryptoService = new RandomEcKeyCryptoService(256); // or some fixed key crypto service
HigherOrderValidationService higherOrderValidationService = new DefaultHigherOrderValidationService();
SchemaValidationService schemaValidationService = new DefaultSchemaValidationService(); // pass "false" to disable fallback schema validation
CborService cborService = new DefaultCborService();
CwtService cwtService = new DefaultCwtService("AT", 24); // validity for HCERT content
CoseService coseService = new DefaultCoseService(cryptoService);
CompressorService compressorService = new DefaultCompressorService(9); // level of compression
Base45Service base45Service = new DefaultBase45Service();
ContextIdentifierService contextIdentifierService = new DefaultContextIdentifierService("HC1:");


Chain chain = new Chain(higherOrderValidationService, schemaValidationService, cborService, cwtService, coseService, compressorService, base45Service, contextIdentifierService);
ChainResult result = chain.encode(input);
```

Implementers may load values for constructor parameters from a configuration file, e.g. with [Spring Boot's configuration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config).

## Logging
Configurability also holds true for logging, which is based on [Napier](https://github.com/AAkira/Napier) and is shipped with a JS+JVM basic debug logger (see [Enabling logging](src/commonTest/kotlin/ehn/techiop/hcert/kotlin/000InitTestContext.kt)).
This should probably be configured differently in production.

As for JS logging, the following functions are available:
- `hcert.setLogLevel(level)`, where `level` is one of the following string values: VERBOSE, DEBUG, INFO, WARNING, ERROR, ASSERT<br>
Setting any other value (or `null`/`undefined`) will disable logging, although it will not *remove* any loggers (see below). By default, a basic console logger is present. Therefore, a call to `setLogLevel` suffices to enable console-based logging.
  The  default logger is exposed as `hcert.defaultLogger`.
- `hcert.setLogger(loggingFunction: (level: String, tag: String?, stackTrace: String?, message: String?) -> Unit,
  keep: Boolean? = false): JsLogger`, which allows for configuring custom logging callbacks.<br>If `keep` is omitted, existing loggers will be replaced, otherwise the newly added logger will be invoked *in addition* to existing loggers. As such, a call to this function without setting `keep=true` will result in the default  console logger to be replaced.<br>
   This function returns the newly created logger instance, which allows for later removal of any added logging callback (see below).
- `hcert.addLogger(logger:Antilog)`/`hcert.removeLogger(logger:Antilog)` functions enable adding/removing any logger created through `setLogger()`, as well as the default console logger exposed though `hcert.defaultLogger`.
It is therefore sensible to store the return value of `setLogger` to cater tor complex logging setups.


On other platforms, Napier's respective default (or custom) platform-specific loggers should be used according to the Napier API.

## Publishing

The library is also published on [jitpack.io](https://jitpack.io/#ehn-dcc-development/hcert-kotlin).

Use it in your project like this:

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ehn-dcc-development.hcert-kotlin:hcert-kotlin-jvm:master-SNAPSHOT'
}
```

If you are planning to use this library, we'll suggest to fork it (internally), and review incoming changes. We can not guarantee non-breaking changes between releases.

### Android

If you plan to use this library on Android versions below 8, be sure to include the following snippet in your `build.gradle`:

```
android {
    defaultConfig {
        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled true
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        coreLibraryDesugaringEnabled true
        // Sets Java compatibility to Java 8
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.1.5'
    // may be needed to make desugaring work in release builds too
    implementation 'org.jetbrains.kotlinx:kotlinx-datetime:0.2.1'
}
```

See these links for details:
 - [Issue 47: DateTime android](https://github.com/ehn-dcc-development/hcert-kotlin/issues/47)
 - [Issue on kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime/issues/97)
 - [Android documentation](https://developer.android.com/studio/write/java8-support#library-desugaring)

## Changelog

Version next:
 - tbd

Version 1.5.0:
 - Add support for schema 1.3.1 & 1.3.2

Version 1.4.0:
 - Add second respository for trust anchors to verify AT-specific vaccination exemptions (prefix AT1:) against (see `buildVerificationChain`)
 - Fix constructors and overloads for Java callers
 - Introduce a debug verification chain
 - Introduce possibility to anonymise personal data (JVM only)
 - Update dependencies:
   - Common: Kotlin: 1.5.31, kotlinx.serialization: 1.3.0, kotlinx.datetime: 0.3.0, Kotest: 4.6.3, Napier (Logging): 2.1.0
   - JVM: Bouncy Castle: 1.69, Json Schema Validation Lib: 2.1.0
   - JS: pako (ZLib): 2.0.4, pkijs: 2.1.97, util: 0.12.4, cbor: 8.0.2, node-inspect-extracted: 1.0.8, ajv (JSON schema validator): 8.6.3, ajv-formats: 2.1.1
 - JS:
   - Switch to upstream cose-js 0.7.0 (deprecates forked version)
   - Fix deprecated calls to `Buffer` constructor (possibly not all calls yet)
   - Switch intermediate (=node) output from CommonJS to UMD 
   - Experimental NodeJS support
     - Enable outputting a bundled js module (UMD) targeting NodeJS if desired
     - the Gradle [npm-publish](https://github.com/mpetuska/npm-publish) plugin does not work as desired

Version 1.3.2:
 - Export `SignedDataDownloader` to JS

Version 1.3.1:
 - Rework verification of timestamps in the HCERT CWT, to work around some weird codes appearing in production
 - New error codes `CWT_NOT_YET_VALID` and `PUBLIC_KEY_NOT_YET_VALID`, see above

Version 1.3.0:
 - Parse a missing `dr` value in HCERT Test entries correctly
 - Add class `SignedData` to hold `content` and `signature` of a TrustList
 - Add services to encode and decode Business Rules (also called Validation Rules)
 - Add services to encode and decode Vaule Sets

Version 1.2.0:
 - Split faulty implementations, sample data, to separate artifact: `ehn.techiop.hcert:hcert-kotlin-jvmdatagen`
 - Add option to get a data class with "nice" names when validating in JS (equivalent to JVM)
 - API change: GreenCertificate now uses arrays for test/vaccination/recovery
 - Add `certificateSubjectCountry` to `VerificationResult`, to get the country of the HCERT's signature certificate
 - Relax schema validation once more to allow explicit `null` values for `nm`, `ma` in HCERT Test entries
 - JS: Fix logging API (previous implementation was incomplete, preventing any logging on JS)

Version 1.1.1:
 - Change `tc` (`testingFacility`) in HCERT Test entries to optional, i.e. nullable String

Version 1.1.0:
 - Try to parse as many dates and datetimes as possible
 - Perform a very relaxed schema validation by default
 - Add errors for trust list loading
 - Support lower Android targets by not using `java.util.Base64`
 - Publish library on jitpack.io

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
 - [Napier](https://github.com/AAkira/Napier), under the Apache-2.0 License
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
