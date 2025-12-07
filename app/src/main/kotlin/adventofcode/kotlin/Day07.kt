package adventofcode.kotlin

object Day07 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day07.txt")!!
            .readText().lines()
        //println(lines)

        val beamIndex = lines[0].indexOf("S")
        val context = Context(beamsIndexes = setOf(beamIndex))
        val result = lines.drop(1).fold(context) { context, line ->
            val splitterIndexes = line.withIndex().filter { (ind, v) -> v == '^' && ind in context.beamsIndexes }.map { it.index }.toSet()
            val newBeamsIndexes = splitterIndexes.flatMap { listOf(it - 1, it + 1) }.toSet()
//            println("Line: $line, splitterIndexes: $splitterIndexes, newBeamsIndexes: $newBeamsIndexes, ctx: $context")
            Context(
                beamsIndexes = context.beamsIndexes + newBeamsIndexes - splitterIndexes,
                splitterCounter = context.splitterCounter + splitterIndexes.size,
            )
        }
        println("Result: ${result.splitterCounter} [${result.beamsIndexes.size}]")

        val timelines = recursiveCounter(
            lines = lines.drop(1),
            row = 1,
            beamIndex = beamIndex,
            timeline = listOf(),
            cache = mutableMapOf()
        )
        println("Number of timelines: $timelines")
    }

    private fun recursiveCounter(lines: List<String>, row: Int, beamIndex: Int, timeline: List<Int>, cache: MutableMap<Pair<Int, Int>, Long>): Long {
        if (lines.isEmpty()) {
//            println(timeline)
            return 1
        }
        if (cache.containsKey(row to beamIndex)) {
            return cache[row to beamIndex] ?: error("should not happen")
        }
        val newLines = lines.drop(1)
        val nextRow = row + 1
        val splitterIndex = lines.first().withIndex().firstOrNull { (ind, v) -> v == '^' && ind == beamIndex }?.index
        if (splitterIndex == null) {
            return recursiveCounter(
                lines = newLines,
                row = nextRow,
                beamIndex = beamIndex,
                timeline = timeline + listOf(beamIndex),
                cache = cache,
            )
        }

        val leftBeamIndex = splitterIndex - 1
        val leftCounter = recursiveCounter(
            lines = newLines,
            row = nextRow,
            beamIndex = leftBeamIndex,
            timeline = timeline + listOf(leftBeamIndex),
            cache = cache,
        )
        cache[nextRow to leftBeamIndex] = leftCounter
        val rightBeamIndex = splitterIndex + 1
        val rightCounter =  recursiveCounter(
            lines = newLines,
            row = nextRow,
            beamIndex = rightBeamIndex,
            timeline = timeline + listOf(rightBeamIndex),
            cache = cache,
        )
        cache[nextRow to rightBeamIndex] = rightCounter
        return leftCounter + rightCounter
    }


    private data class Context(
        val beamsIndexes: Set<Int> = emptySet(),
        val splitterCounter: Int = 0,
    )
}