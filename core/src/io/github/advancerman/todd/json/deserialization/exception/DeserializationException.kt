package io.github.advancerman.todd.json.deserialization.exception

import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.deserialization.getJsonErrorMessage

class DeserializationException(json: JsonValue, message: String, cause: Throwable? = null) :
    RuntimeException(getJsonErrorMessage(json, message), cause)
