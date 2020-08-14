package com.company.todd.json

import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.passive.Level
import com.company.todd.util.files.crawlJsonListsWithComments
import com.company.todd.util.LEVELS_PATH

fun loadLevels() =
        crawlJsonListsWithComments(assetsFolder + LEVELS_PATH).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { parseInGameObject(it) }.toMutableList())
        }
