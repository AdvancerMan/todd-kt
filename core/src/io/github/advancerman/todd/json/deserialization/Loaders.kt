package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.objects.passive.Level
import io.github.advancerman.todd.util.files.crawlJsonListsWithComments
import io.github.advancerman.todd.util.LEVELS_PATH
import io.github.advancerman.todd.util.SETTINGS_PATH
import io.github.advancerman.todd.util.files.toOsDependentPath

fun loadLevels() =
        crawlJsonListsWithComments(LEVELS_PATH).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { parseInGameObject(it) }.toMutableList())
        }

val jsonSettings: JsonValue by lazy {
    JsonReader().parse(Gdx.files.internal(SETTINGS_PATH.toOsDependentPath()).readString())
}
