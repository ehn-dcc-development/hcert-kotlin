# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain:
 - Encode in CBOR
 - Sign in COSE
 - Compress with ZLib
 - Prepend with national identifier
 - Encode as QR Code and Aztec Code

## TODO

- Use the JSON schema for data classes

## Publishing

To publish this package to GitHub, create a personal access token (read <https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages>), and add `gpr.user` and `gpr.key` in your `~/.gradle/gradle.properties` and run `./gradlew publish`
