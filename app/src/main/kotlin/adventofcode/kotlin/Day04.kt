package adventofcode.kotlin

object Day04 {

    private val pairs = listOf(
        -1 to -1,
        -1 to 0,
        -1 to 1,
        0 to -1,
        0 to 1,
        1 to -1,
        1 to 0,
        1 to 1,
    )

    fun solve() {
        val lines = javaClass.classLoader.getResource("day04.txt")!!
            .readText().lines().filter { it.isNotBlank() }
        // println(lines)

        val result1 = findAccessibleRolls(lines = lines)
        println("Part 1: ${result1.size}")
        println("Part 2: ${loopAccessibleRolls(lines = lines)}")
    }

    private fun loopAccessibleRolls(lines: List<String>): Int {
        val accessibleRolls = findAccessibleRolls(lines = lines)
        if (accessibleRolls.isEmpty()) {
            return 0
        }
        return accessibleRolls.size + loopAccessibleRolls(
            lines = replaceAccessible(
                lines = lines,
                accessible = accessibleRolls
            )
        )
    }

    private fun replaceAccessible(
        lines: List<String>,
        accessible: Set<Pair<Int, Int>>
    ): List<String> = lines.mapIndexed { rowIndex, line ->
        line.withIndex().fold(StringBuilder()) { builder, (colIndex, e) ->
            builder.append(if (rowIndex to colIndex in accessible) '.' else e)
        }.toString()
    }

    private fun findAccessibleRolls(lines: List<String>): Set<Pair<Int, Int>> {
        return sequence<Pair<Int, Int>> {
            for ((lineInd, line) in lines.withIndex()) {
                for ((columnInd, ch) in line.withIndex()) {
//                    println("Working with [$lineInd,$columnInd]=$ch")
                    if (ch != '@') {
                        continue
                    }
                    val elements = pairs.mapNotNull { (l, r) ->
                        val checkLine = lineInd + l
                        val checkColumn = columnInd + r
                        if (checkLine !in 0 until lines.size || checkColumn !in 0 until line.length) {
                            return@mapNotNull null
                        }
                        lines[checkLine][checkColumn]
                    }
//                    println("Neighbours[${elements.size}]: $elements")
                    val rolls = elements.filter { it == '@' }
                    if (rolls.size < 4) {
                        yield(lineInd to columnInd)
                    }
                }
            }
        }.toSet()
    }
}