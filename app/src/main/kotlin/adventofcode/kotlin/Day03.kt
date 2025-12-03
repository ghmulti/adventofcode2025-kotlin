package adventofcode.kotlin

object Day03 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day03.txt")!!
            .readText().lines().filter { it.isNotBlank() }
        println(lines)

        val joltages = lines.map { line ->
            findHighestJoltage(
                remainingNumbers = 2,
                line = line
            )
        }
        println(joltages)
        println("Sum: ${joltages.sum()}")

        val joltages2 = lines.map { line ->
            findHighestJoltage(
                remainingNumbers = 12,
                line = line
            )
        }
        println(joltages2)
        println("Sum: ${joltages2.sum()}")
    }

    private fun findHighestJoltage(acc: String = "", remainingNumbers: Int, line: String): Long {
        if (remainingNumbers == 0) {
            return acc.toLong()
        }
        val a = line.toCharArray().mapIndexed { i, c -> i to c.digitToInt() }
        val maxPair = a.dropLast(remainingNumbers-1).maxByOrNull { (_, c) -> c } ?: error("not found")
        return findHighestJoltage(
            acc = acc + maxPair.second.toString(),
            remainingNumbers = remainingNumbers-1,
            line = line.drop(maxPair.first+1)
        )
    }
}