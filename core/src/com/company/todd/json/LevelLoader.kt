package com.company.todd.json

import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.passive.Level
import com.company.todd.asset.texture.checkContains
import com.company.todd.util.files.crawlJsonListsWithComments

const val levelsPath = "levels/"

fun loadLevels() =
        crawlJsonListsWithComments(assetsFolder + levelsPath).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { parseInGameObject(it) }.toMutableList())
        }
