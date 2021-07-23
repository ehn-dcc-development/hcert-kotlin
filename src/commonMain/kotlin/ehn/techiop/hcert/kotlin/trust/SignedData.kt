package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys

/**
 * Holds a [content] and detached [signature] in a certain format:
 * [signature] is an encoded COSE structure with protected header keys
 * - [CoseHeaderKeys.TRUSTLIST_VERSION]
 * - [CoseHeaderKeys.KID]: KID of the signer certificate
 * The content of that COSE structure is a CWT map containing
 * - [CwtHeaderKeys.NOT_BEFORE]: seconds since UNIX epoch
 * - [CwtHeaderKeys.EXPIRATION]: seconds since UNIX epoch
 * - [CwtHeaderKeys.SUBJECT]: the SHA-256 hash of the content file
 */
typealias SignedData = Pair<ByteArray,ByteArray>
val SignedData.content: ByteArray get() = this.first
val SignedData.signature: ByteArray get() = this.second

