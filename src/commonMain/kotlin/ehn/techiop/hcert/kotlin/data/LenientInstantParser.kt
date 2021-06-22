package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Some countries encode Instants in a wrong format,
 * e.g. missing "Z" or the offset "+0200" instead of "+02:00",
 * so we'll try to work around those issues
 */
object LenientInstantParser : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val value = decoder.decodeString()
        val fixOffset = value.replace(Regex("\\+(\\d{2})(\\d{2})")) { "+${it.groupValues[1]}:${it.groupValues[2]}" }
        val fixZulu = if (fixOffset.contains('Z') || fixOffset.contains("+")) fixOffset else fixOffset + 'Z'
        return Instant.parse(fixZulu)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}

