package adventofcode.kotlin

object Day05 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day05.txt")!!
            .readText().lines()

        val ranges = lines.takeWhile { it.isNotBlank() }
            .map {
                val r = it.split("-")
                r[0].toLong()..r[1].toLong()
            }
//        println(ranges)

        val ids = lines.dropWhile { it.isNotBlank() }
            .filter { it.isNotBlank() }
            .map { it.toLong() }
//        println(ids)

        val fresh = ids.filter { id ->
            ranges.any { ranges -> id in ranges }
        }
        println("Fresh: ${fresh.size}")

        val sortedRanges = ranges.sortedBy { it.first }
//        println(sortedRanges)

        val result = sortedRanges.fold(Acc(currentRange = sortedRanges.first())) { acc, range ->
            when {
                range.first > acc.currentRange.last -> {
                    Acc(
                        ranges = acc.combineRanges(),
                        currentRange = range
                    )
                }
                range.last < acc.currentRange.last -> acc
                range.last > acc.currentRange.last -> {
                    Acc(
                        ranges = acc.ranges,
                        currentRange = acc.currentRange.first..range.last,
                    )
                }
                else -> acc
            }
        }.combineRanges()
//        println(result)
        println(result.sumOf { it.last - it.first + 1 })
    }

    private data class Acc(
        val ranges: List<LongRange> = emptyList(),
        val currentRange: LongRange,
    ) {
        fun combineRanges(): List<LongRange> {
            return ranges + listOf(currentRange)
        }
    }
}