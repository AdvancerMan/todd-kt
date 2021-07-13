package com.company.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.objects.passive.Level
import com.company.todd.util.files.crawlJsonListsWithComments
import com.company.todd.util.LEVELS_PATH
import com.company.todd.util.SETTINGS_PATH
import com.company.todd.util.files.toOsDependentPath

fun loadLevels() =
        crawlJsonListsWithComments(LEVELS_PATH).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { parseInGameObject(it) }.toMutableList())
        }

val jsonSettings: JsonValue by lazy {
    JsonReader().parse(Gdx.files.internal(SETTINGS_PATH.toOsDependentPath()).readString())
}
