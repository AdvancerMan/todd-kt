package com.company.todd.objects.passive.level

import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.checkContains
import com.company.todd.util.files.crawlJsonListsWithComments

const val levelsPath = "levels/"

private val stringInfos = PassiveObjectInfo.values().map { it.toString() }

fun loadLevels() =
        crawlJsonListsWithComments(assetsFolder + levelsPath).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { jsonToObjectInfo(it) }.toMutableList())
        }

private fun jsonToObjectInfo(objectJson: JsonValue): (ToddGame) -> PassiveObject {
    checkContains(objectJson, "type", "one of strings: $stringInfos") {
        it.isString && stringInfos.contains(it.asString())
    }
    val info = PassiveObjectInfo.valueOf(objectJson["type"].asString())
    checkContains(objectJson, "args", "array of $info arguments") { it.isArray }
    val argsJson = objectJson["args"]

    return { info.constructor(it, it.textureManager.loadDrawable(info.drawableName), argsJson) }
}
