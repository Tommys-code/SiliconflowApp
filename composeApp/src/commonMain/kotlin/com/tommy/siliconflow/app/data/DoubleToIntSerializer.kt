package com.tommy.siliconflow.app.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Suppress("EXTERNAL_SERIALIZER_USELESS")
@Serializer(forClass = Int::class)
object DoubleToIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Int", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val value = decoder.decodeDouble()
        return value.toInt() // 安全转换：3.0 → 3，3.5 → 3（向下取整）
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }
}