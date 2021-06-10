package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.trust.ContentType

internal object CryptoServiceHolder {

    data class MapKey(val keyType: KeyType, val keySize: Int, val contentType: ContentType?)

    val map = mutableMapOf<MapKey, CryptoService>()

    fun getRandomCryptoService(type: KeyType, keySize: Int, contentType: ContentType?): CryptoService {
        val mapKey = MapKey(type, keySize, contentType)
        val cryptoService = map[mapKey]
        if (cryptoService != null) {
            return cryptoService
        }
        val safeContentTypes = if (contentType != null) listOf(contentType) else ContentType.values().toList()
        val newService = when (type) {
            KeyType.EC -> RandomEcKeyCryptoService(keySize, safeContentTypes)
            else -> RandomRsaKeyCryptoService(keySize, safeContentTypes)
        }
        map[mapKey] = newService
        return newService
    }
}