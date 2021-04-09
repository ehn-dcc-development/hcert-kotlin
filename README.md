# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain:
 - Encode in CBOR
 - Sign in COSE
 - Compress with ZLib
 - Prepend with national identifier
 - Encode as QR Code and Aztec Code

## TODO

- Use the JSON schema for data classes
