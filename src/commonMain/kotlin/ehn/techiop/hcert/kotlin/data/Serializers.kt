package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/*
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}*/
/*
@Serializer(forClass = Instant::class)
object InstantLongSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochSecond(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSecond)
    }
}

@Serializer(forClass = Instant::class)
object InstantStringSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoder.decodeString(), Instant::from)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}*/

/*
@Serializer(forClass = X509Certificate::class)
object X509CertificateSerializer : KSerializer<X509Certificate> {

    private val certificateFactory = CertificateFactory.getInstance("X.509")

    override fun deserialize(decoder: Decoder): X509Certificate {
        val inputStream = decoder.decodeString().fromBase64().inputStream()
        return certificateFactory.generateCertificate(inputStream) as X509Certificate
    }

    override fun serialize(encoder: Encoder, value: X509Certificate) {
        encoder.encodeString(value.encoded.asBase64())
    }
}

*/
@Serializer(forClass = ValueSetEntryAdapter::class)
object ValueSetEntryAdapterSerializer : KSerializer<ValueSetEntryAdapter> {
    override fun deserialize(decoder: Decoder): ValueSetEntryAdapter {
        return ValueSetsInstanceHolder.INSTANCE.find(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ValueSetEntryAdapter) {
        encoder.encodeString(value.key)
    }
}

