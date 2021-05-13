package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
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

/*@ExperimentalSerializationApi
@Serializer(forClass = Instant::class)
object InstantLongSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return LocalDateTime.parse(decoder.decodeString()).toInstant(TimeZone.UTC)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toLocalDateTime(TimeZone.UTC).toString())
    }
}

@ExperimentalSerializationApi
@Serializer(forClass = Instant::class)
object InstantStringSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSeconds)
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

@ExperimentalSerializationApi
@Serializer(forClass = ValueSetEntryAdapter::class)
object ValueSetEntryAdapterSerializer : KSerializer<ValueSetEntryAdapter> {
    override fun deserialize(decoder: Decoder): ValueSetEntryAdapter {
        return ValueSetsInstanceHolder.INSTANCE.find(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ValueSetEntryAdapter) {
        encoder.encodeString(value.key)
    }
}

