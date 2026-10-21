package com.shushly.app.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ProtectionLevelSerializer : KSerializer<ProtectionLevel> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ProtectionLevel", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ProtectionLevel) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): ProtectionLevel {
        val raw = decoder.decodeString()
        return ProtectionLevel.fromString(raw)
    }
}

@Serializable(with = ProtectionLevelSerializer::class)
enum class ProtectionLevel(
    val title: String,
    val subtitle: String,
    val dohUrl: String,
    val filterName: String
) {
    LIGHT(
        title = "Light",
        subtitle = "Everyday ad and tracker blocking with maximum compatibility",
        dohUrl = "https://freedns.controld.com/p2",
        filterName = "Control D Ads & Tracking"
    ),
    BALANCED(
        title = "Balanced",
        subtitle = "Comprehensive ad and tracking protection with OISD Full",
        dohUrl = "https://freedns.controld.com/x-oisd",
        filterName = "Control D OISD Full"
    ),
    STRICT(
        title = "Strict",
        subtitle = "Aggressive blocking for advanced privacy using HaGeZi Pro",
        dohUrl = "https://freedns.controld.com/x-hagezi-pro",
        filterName = "Control D HaGeZi Pro"
    );

    companion object {
        fun fromString(value: String?): ProtectionLevel = when (value?.uppercase()?.trim()) {
            "LIGHT", "DEFAULT" -> LIGHT
            "STRICT", "STRONG" -> STRICT
            "BALANCED" -> BALANCED
            else -> LIGHT
        }
    }
}
