package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Some memberstate tests from dgc-testdata actually don't include the Zulu time zone marker,
 * and some include the offset wrongly as "+0200" instead of "+02:00"
 */
object LenientInstantParser : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val value = decoder.decodeString()
        val offsetFixed = value.replace(Regex("\\+(\\d{2})(\\d{2})")) { "+${it.groupValues[1]}:${it.groupValues[2]}" }
        val fixed = if (offsetFixed.contains('Z') || offsetFixed.contains("+")) offsetFixed else offsetFixed + 'Z'
        return Instant.parse(fixed)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}

