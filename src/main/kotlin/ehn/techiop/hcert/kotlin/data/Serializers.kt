package ehn.techiop.hcert.kotlin.data

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
import java.time.format.DateTimeFormatter


@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}

@Serializer(forClass = Instant::class)
object InstantStringSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        val string = decoder.decodeString()
        try {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(string, Instant::from)
        } catch (e: Exception) {
            return Instant.parse(if (string.contains('Z')) string else string + 'Z')
        }
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}

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

@Serializer(forClass = ValueSetEntryAdapter::class)
object ValueSetEntryAdapterSerializer : KSerializer<ValueSetEntryAdapter> {
    override fun deserialize(decoder: Decoder): ValueSetEntryAdapter {
        return ValueSetHolder.INSTANCE.find(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ValueSetEntryAdapter) {
        encoder.encodeString(value.key)
    }
}

