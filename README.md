# Electronic Health Certificate Kotlin Library

Implements a very basic validation and creation chain:
 - Encode in CBOR
 - Sign in COSE
 - Compress with ZLib
 - Prepend with context identifier
 - Encode as QR Code

All services are implemented according to the [Specification 1.0.5](https://github.com/ehn-digital-green-development/hcert-spec).

## TODO

- Use the JSON schema for data classes

## Publishing

To publish this package to GitHub, create a personal access token (read <https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages>), and add `gpr.user` and `gpr.key` in your `~/.gradle/gradle.properties` and run `./gradlew publish`

## Libraries

This library uses the following dependencies:
 - [Kotlin](https://github.com/JetBrains/kotlin), under the Apache-2.0 License
 - [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization), under the Apache-2.0 License
 - [COSE-JAVA](https://github.com/cose-wg/cose-java), under the BSD-3-Clause License
 - [ZXing](https://github.com/zxing/zxing), under the Apache-2.0 License
 - [Bouncycastle](https://github.com/bcgit/bc-java), under an [MIT-compatible license](https://www.bouncycastle.org/licence.html)
 - [OkHttp](https://github.com/square/okhttp), under the Apache-2.0 License
 - [JUnit](https://github.com/junit-team/junit5), under the Eclipse Public License v2.0
 - [Hamcrest](https://github.com/hamcrest/JavaHamcrest), under the BSD-3-Clause License
