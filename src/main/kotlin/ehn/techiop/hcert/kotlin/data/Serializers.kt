package ehn.techiop.hcert.kotlin.data

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.fromBase64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE))
    }
}

@Serializer(forClass = OffsetDateTime::class)
object IsoOffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE_TIME)
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE_TIME))
    }
}

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochSecond(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSecond)
    }
}

@Serializer(forClass = X509Certificate::class)
object X509CertificateSerializer : KSerializer<X509Certificate> {
    override fun deserialize(decoder: Decoder): X509Certificate {
        return CertificateFactory.getInstance("X.509")
            .generateCertificate(decoder.decodeString().fromBase64().inputStream()) as X509Certificate
    }

    override fun serialize(encoder: Encoder, value: X509Certificate) {
        encoder.encodeString(value.encoded.asBase64())
    }
}


@Serializer(forClass = Eudgc::class)
object EudgcSerializer : KSerializer<Eudgc> {
    override fun deserialize(decoder: Decoder): Eudgc {
        return ObjectMapper().readValue(decoder.decodeString(), Eudgc::class.java)
    }

    override fun serialize(encoder: Encoder, value: Eudgc) {
        encoder.encodeString(ObjectMapper().writeValueAsString(value))
    }
}

@Serializer(forClass = ValueSetEntryAdapter::class)
object ValueSetEntryAdapterSerializer : KSerializer<ValueSetEntryAdapter> {
    override fun deserialize(decoder: Decoder): ValueSetEntryAdapter {
        return ValueSetHolder.INSTANCE.find(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ValueSetEntryAdapter) {
        encoder.encodeString(value.key)
    }
}

