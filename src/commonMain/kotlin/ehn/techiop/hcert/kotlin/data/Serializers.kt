package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializer(forClass = ValueSetEntryAdapter::class)
object ValueSetEntryAdapterSerializer : KSerializer<ValueSetEntryAdapter> {
    override fun deserialize(decoder: Decoder): ValueSetEntryAdapter {
        return ValueSetEntryAdapter(decoder.decodeString(), ValueSetEntry("foo", "de", true, "system", "version"))
        //TODO Implement ValueSetEntryAdapterSerializer
        //return ValueSetsInstanceHolder.INSTANCE.find(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ValueSetEntryAdapter) {
        encoder.encodeString(value.key)
    }
}

