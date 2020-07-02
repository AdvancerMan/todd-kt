package com.company.todd.util.files

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.Queue
import com.company.todd.launcher.assetsFolder
import java.util.regex.Pattern

fun crawl(fileNamePattern: Pattern, internalPath: String): List<Pair<String, String>> {
    val res = mutableListOf<Pair<String, String>>()
    val queue = Queue<FileHandle>()
    queue.addLast(Gdx.files.internal(internalPath))

    while (queue.notEmpty()) {
        val files = queue.removeFirst().list()
        files.filter { it.isDirectory }
                .forEach { queue.addLast(it) }
        files.filter { !it.isDirectory && fileNamePattern.matcher(it.name()).matches() }
                .map { it.name() to it.readString() }
                .let { res.addAll(it) }
    }

    return res
}

val commentsPattern = Pattern.compile("(//|#)[^\\n]*\\n?")!!

fun removeComments(input: String) =
        commentsPattern.matcher(input).replaceAll("\n")!!

private val jsonReader = JsonReader()
private val jsonPattern = Pattern.compile(".*\\.json")

fun crawlJsonListsWithComments(internalPath: String) =
        crawl(jsonPattern, internalPath)
                .flatMap {
                    removeComments(it.second)
                            .trim()
                            .let { json ->
                                jsonReader.parse(if (json[0] == '[') json else "[$json]")
                            }
                }

fun getLevels() = crawlJsonListsWithComments(assetsFolder + "levels")

fun getAnimationInfos() = crawlJsonListsWithComments(assetsFolder + "pics")
