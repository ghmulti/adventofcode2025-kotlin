package adventofcode.kotlin

object Day11 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day11.txt")!!
            .readText().lines().filter { it.isNotBlank() }
//        println(lines)

        val parsedLines = parseLines(lines)
//        println(parsedLines)

        val paths1 = findPathsBFS(dict = parsedLines)
        println("Result1: $paths1")

        val paths2 = findPathRecursive(
            wrapper = DeviceWrapper(
                device = "svr",
                hasDac = false,
                hasFft = false,
            ),
            input = parsedLines,
            cache = mutableMapOf(),
        )
        println("Result2: $paths2")
    }

    private data class DeviceWrapper(val device: String, val hasDac: Boolean, val hasFft: Boolean)

    private fun findPathRecursive(
        wrapper: DeviceWrapper,
        input: Map<String, List<String>>,
        cache: MutableMap<DeviceWrapper, Long>,
    ): Long {
        if (cache.containsKey(wrapper)) {
            return cache[wrapper]!!
        }

        val newEntries = input[wrapper.device] ?: error("???")
        val counter = newEntries.fold(0L) { acc, entry ->
            val toAdd = when {
                entry != "out" -> {
                    findPathRecursive(
                        wrapper = DeviceWrapper(
                            device = entry,
                            hasDac = wrapper.hasDac || entry == "dac",
                            hasFft = wrapper.hasFft || entry == "fft"
                        ),
                        input = input,
                        cache = cache
                    )
                }
                wrapper.hasDac && wrapper.hasFft -> 1L
                else -> 0L
            }
            acc + toAdd
        }

        cache[wrapper] = counter
        return counter
    }

    private fun findPathsBFS(
        dict: Map<String, List<String>>,
    ): Int {
        val from = "you"
        val to = "out"

        val start = listOf(from)
        val stack = ArrayDeque<List<String>>().apply { add(start) }
        var counter = 0

        while (stack.isNotEmpty()) {
            val currentPath = stack.removeLast()
            val lastElement = currentPath.last()

            val newElements = dict[lastElement]!!
            val newPaths = newElements.map { currentPath + it }

            val finishedPaths = newPaths.filter { it.last() == to }
            counter += finishedPaths.size

            val notFinished = newPaths.filter { it.last() != to }
            stack.addAll(notFinished)
        }

        return counter
    }

    private fun parseLines(lines: List<String>): Map<String, List<String>> {
        return lines.associate { line ->
            val parts = line.split(": ")
            parts[0] to parts[1].split(" ")
        }
    }
}