package com.company.todd.json.serialization

import com.badlogic.gdx.utils.JsonValue

interface ManuallyJsonSerializable {
    fun serializeUpdates(json: JsonValue)
    fun serializeFull(json: JsonValue)
}
