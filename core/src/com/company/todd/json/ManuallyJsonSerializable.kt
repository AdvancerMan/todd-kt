package com.company.todd.json

import com.badlogic.gdx.utils.JsonValue

interface ManuallyJsonSerializable {
    fun serializeUpdates(json: JsonValue)
    fun deserializeUpdates(json: JsonValue)
    fun serializeFull(json: JsonValue)
}
