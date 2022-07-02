package com.ivyxjc.kotwarden.model.serializer

import com.ivyxjc.kotwarden.util.format
import com.ivyxjc.kotwarden.util.parse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime

class OffsetDatetimeNullableSerializer : KSerializer<OffsetDateTime?> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OffsetDatetime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime? {
        return parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime?) {
        encoder.encodeString(format(value))
    }
}

class OffsetDatetimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OffsetDatetime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return parse(decoder.decodeString())!!
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(format(value))
    }
}