package adventofcode.kotlin

object Day01 {

    data class Dial (
        val pointer: Int = 50,
        val zerosPointers: Int = 0,
        val zerosPassing: Int = 0,
    ) {
        fun process(entry: String): Dial {
            val direction = if (entry[0] == 'R') 1 else -1
            val absStep = entry.substring(1).toInt()
            val step = (absStep * direction) % 100
            val newPointer = (pointer + step + 100) % 100
            val passedZero = if (pointer == 0 || pointer + step in 1..99) 0 else 1
            return Dial(
                pointer = newPointer,
                zerosPointers = zerosPointers + if (newPointer == 0) 1 else 0,
                zerosPassing = zerosPassing + (absStep / 100) + passedZero
            )
        }
    }

    fun solve() {
        val lines = javaClass.classLoader.getResource("day01.txt")!!
            .readText().lines().filter { it.isNotBlank() }
        val result = lines.fold(Dial()) { acc, line -> acc.process(line) }
        println(result)
    }

}