import java.io.File
import java.lang.Exception

enum class SearchStrategy() {
    ALL,
    ANY,
    NONE,
    NULL
}

fun showMenu(): Int {
    println("=== Menu ===\n" +
            "1. Find a person\n" +
            "2. Print all people\n" +
            "0. Exit"
    )

    return readln().toInt()
}

fun checkSelectedItem(item: Int): Boolean {
    if (item > 2) {
        println("Incorrect option! Try again.\n")
        return false
    }

    return true
}

fun printAllLines(lines: List<String>) {
    println("=== List of people ===")
    for (line in lines) {
        println(line)
    }
    println("")
}

fun findLinesByIndexSearchStrategyAll(targetLine: String, lines: List<String>, index: Map<String, List<Int>>) {
    var wordsNumber = 0
    val searchResults = mutableMapOf<Int, MutableList<String>>()
    targetLine.split(" ").forEach {
        targetWord ->
        wordsNumber++
        if (index.containsKey(targetWord)) {
            index.getValue(targetWord).forEach {
                lineNumber ->
                if (searchResults.containsKey(lineNumber)) {
                    searchResults.getValue(lineNumber).add(targetWord)
                }
                else {
                    searchResults[lineNumber] = mutableListOf(targetWord)
                }
            }
        }
    }

    for (searchResult in searchResults) {
        if (searchResult.value.size != wordsNumber) {
            searchResults.remove(searchResult.key)
        }
    }

    println("\n${searchResults.size} persons found:")
    for (searchResult in searchResults) {
        println(lines[searchResult.key])
    }

    println("")
}

fun findLinesByIndexSearchStrategyAny(targetLine: String, lines: List<String>, index: Map<String, List<Int>>) {
    val searchResults = mutableMapOf<Int, MutableList<String>>()
    targetLine.split(" ").forEach {
        targetWord ->
        if (index.containsKey(targetWord)) {
            index.getValue(targetWord).forEach {
                    lineNumber ->
                if (searchResults.containsKey(lineNumber)) {
                    searchResults.getValue(lineNumber).add(targetWord)
                }
                else {
                    searchResults[lineNumber] = mutableListOf(targetWord)
                }
            }
        }
    }

    println("\n${searchResults.size} persons found:")
    for (searchResult in searchResults) {
        println(lines[searchResult.key])
    }

    println("")
}

fun findLinesByIndexSearchStrategyNone(targetLine: String, lines: List<String>, index: Map<String, List<Int>>) {
    val searchResults = mutableMapOf<Int, MutableList<String>>()
    targetLine.split(" ").forEach {
        targetWord ->
        if (index.containsKey(targetWord)) {
            index.getValue(targetWord).forEach {
                    lineNumber ->
                if (searchResults.containsKey(lineNumber)) {
                    searchResults.getValue(lineNumber).add(targetWord)
                }
                else {
                    searchResults[lineNumber] = mutableListOf(targetWord)
                }
            }
        }
    }

    println("\n${lines.size - searchResults.size} persons found:")
    for (idx in lines.indices) {
        if (searchResults.containsKey(idx)) {
            continue
        }

        println(lines[idx])
    }

    println("")
}

fun findLinesByIndex(lines: List<String>, index: Map<String, List<Int>>, searchStrategy: SearchStrategy) {
    println("\nEnter a name or email to search all matching people.")
    val targetLine = readln().lowercase()
    when (searchStrategy) {
        SearchStrategy.ALL -> {
            findLinesByIndexSearchStrategyAll(targetLine, lines, index)
        }
        SearchStrategy.ANY -> {
            findLinesByIndexSearchStrategyAny(targetLine, lines, index)
        }
        SearchStrategy.NONE -> {
            findLinesByIndexSearchStrategyNone(targetLine, lines, index)
        }
        SearchStrategy.NULL -> {
            println("Invalid search strategy! Try again.\n")
            return
        }
    }
}

fun readDataFromFile(filePath: String): MutableList<String> {
    val lines = mutableListOf<String>()
    File(filePath).forEachLine { infoLine -> lines.add(infoLine) }

    return lines
}

fun buildInvertedIndex(lines: List<String>): Map<String, MutableList<Int>> {
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()
    for (idx in lines.indices) {
        lines[idx].split(" ").forEach{
                word ->
            val lowerCasedWord = word.lowercase()
            if (invertedIndex.containsKey(lowerCasedWord)) {
                invertedIndex.getValue(lowerCasedWord).add(idx)
            }
            else {
                invertedIndex[lowerCasedWord] = mutableListOf(idx)
            }
        }
    }

    return invertedIndex
}

fun selectSearchStrategy(): SearchStrategy {
    println("\nSelect a matching strategy: ALL, ANY, NONE")
    val selectedSearchStrategy = readln()
    for (searchStrategy in SearchStrategy.values()) {
        if (searchStrategy.name == selectedSearchStrategy) {
            return searchStrategy
        }
    }

    return SearchStrategy.NULL
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        throw Exception("Invalid arguments size. Please, provide --data argument.")
    }

    if (args[0] != "--data") {
        throw Exception("Invalid program arguments. Please, provide --data argument.")
    }

    val lines: MutableList<String>
    try {
        lines = readDataFromFile(args[1])
    } catch (e: java.io.IOException) {
        println(e.message)
        return
    }

    val index = buildInvertedIndex(lines)

    while (true) {
        val selectedItem = showMenu()
        if (!checkSelectedItem(selectedItem)) {
            continue
        }

        when (selectedItem) {
            0 -> break
            2 -> printAllLines(lines)
            1 -> {
                val selectSearchStrategy = selectSearchStrategy()
                findLinesByIndex(lines, index, selectSearchStrategy)
            }
        }
    }

    println("Bye!")
}