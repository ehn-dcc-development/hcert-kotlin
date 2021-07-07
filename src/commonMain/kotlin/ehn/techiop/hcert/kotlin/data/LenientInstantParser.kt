package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Tries to parse an [Instant], see [InstantParser.parseInstant]
 */
object LenientInstantParser : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        return InstantParser.parseInstant(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}


/**
 * Tries to parse an [Instant] that may be null, see [InstantParser.parseInstant]
 */
object LenientNullableInstantParser : KSerializer<Instant?> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant?", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant? {
        return try {
            InstantParser.parseInstant(decoder.decodeString())
        } catch (e: Throwable) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Instant?) {
        if (value != null) {
            encoder.encodeString(value.toString())
        } else {
            encoder.encodeNull()
        }
    }

}


private object InstantParser {

    /**
     * Some countries encode Instants in a wrong format,
     * e.g. missing "Z" or the offset "+0200" instead of "+02:00",
     * so we'll try to work around those issues
     */
    fun parseInstant(value: String): Instant {
        val fixOffset = value.replace(Regex("\\+(\\d{2})(\\d{2})")) { "+${it.groupValues[1]}:${it.groupValues[2]}" }
        val fixZulu = if (fixOffset.contains('Z') || fixOffset.contains("+")) fixOffset else fixOffset + 'Z'
        return Instant.parse(fixZulu)
    }

}