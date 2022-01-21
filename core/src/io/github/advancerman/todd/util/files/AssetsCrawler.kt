package io.github.advancerman.todd.util.files

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Queue
import java.io.File
import java.util.regex.Pattern

fun String.toOsDependentPath() = split('/').joinToString(File.separator)

fun crawl(fileNamePattern: Pattern, unixInternalPath: String): List<Pair<String, String>> {
    val res = mutableListOf<Pair<String, String>>()
    val queue = Queue<FileHandle>()
    queue.addLast(Gdx.files.internal(unixInternalPath.toOsDependentPath()))

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

private val commentsPattern = Pattern.compile("//[^\\n]*\\n?")!!

fun String.removeComments() = commentsPattern.matcher(this).replaceAll("\n")!!

private val jsonPattern = Pattern.compile(".*\\.json")

fun crawlJsonListsWithComments(unixInternalPath: String): List<JsonValue> {
    val jsonReader = JsonReader()
    return crawl(jsonPattern, unixInternalPath)
            .flatMap { fileNameToJson ->
                fileNameToJson.second
                        .removeComments()
                        .trim()
                        .let {
                            if (it.isEmpty()) {
                                listOf()
                            } else {
                                jsonReader.parse(if (it[0] == '[') it else "[$it]")
                            }
                        }
            }
}
