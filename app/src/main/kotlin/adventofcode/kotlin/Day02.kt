package adventofcode.kotlin

object Day02 {

    fun solve() {
        val ranges = javaClass.classLoader.getResource("day02.txt")!!
            .readText().trim()
            .split(",")
            .filter { it.isNotBlank() }
            .map { rng ->
                val a = rng.split("-")
                a[0].toLong()..a[1].toLong()
            }
        println(ranges)

        val invalidIds1 = ranges.flatMap { range ->
            range.filter(::isInvalid)
        }
        println("Sum invalid ids 1: ${invalidIds1.sum()}")
        val invalidIds2 = ranges.flatMap { range ->
            range.filter(::isInvalid2)
        }
        println("Sum invalid ids 2: ${invalidIds2.sum()}")
    }

    fun isInvalid(e: Long): Boolean {
        val str = e.toString()
        if (str.length % 2 != 0) {
            return false
        }
        return str.take(str.length / 2) == str.substring(str.length / 2)
    }

    fun isInvalid2(e: Long): Boolean {
        val str = e.toString()
        for (i in 0..str.length/2) {
            val sq = str.take(i)
            if (str.replace(sq, "").isBlank()) {
                return true
            }
        }
        return false
    }

}