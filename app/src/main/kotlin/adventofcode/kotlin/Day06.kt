package adventofcode.kotlin

import java.util.regex.Pattern

object Day06 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day06.txt")!!
            .readText().lines()
//        println(lines)
        val operation = lines.last().split(Pattern.compile("\\s+")).map { it.trim() }
            .map { sign ->
                when (sign) {
                    "+" -> { e1: Long, e2: Long -> e1 + e2 }
                    "*" -> { e1: Long, e2: Long -> e1 * e2 }
                    else -> error("unknown sign: $sign")
                }
            }
//        println(operation)
        val numbers1 = lines.dropLast(1).map { e ->
            e.split(Pattern.compile("\\s+")).mapNotNull { it.trim().toLongOrNull() }
        }
//        println(numbers)

        val results1 = operation.mapIndexed { index, operation ->
            numbers1.map { line -> line[index] }.reduce(operation)
        }
        println(results1)
        println("Sum results: ${results1.sum()}")

        val source = lines.dropLast(1).map { it.reversed() }
        val numbers2 = sequence {
            val elements = mutableListOf<Long>()
            for (i in lines.first().indices) {
                val num = source.map { line -> line[i] }.joinToString(separator = "")
                if (num.isBlank()) {
                    yield(elements.toList())
                    elements.clear()
                    continue
                }
                elements.add(num.trim().toLong())
            }
            yield(elements.toList())
        }.toList().reversed()
//        println(numbers2)
        val results2 = operation.mapIndexed { index, operation ->
            numbers2[index].reduce(operation)
        }
//        println(results2)
        println("Sum results: ${results2.sum()}")
    }
}