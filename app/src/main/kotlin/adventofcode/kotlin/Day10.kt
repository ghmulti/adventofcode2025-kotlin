package adventofcode.kotlin

object Day10 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day10.txt")!!
            .readText().lines().filter { it.isNotBlank() }
//        println(lines)

        val machines = parseMachines(lines)
        println(machines.first())

        val minNumberOfPresses = machines.sumOf(::findMinNumberOfButtonPresses)
        println("Result1: $minNumberOfPresses")
    }

    private fun findMinNumberOfButtonPresses(machine: Machine): Int {
        val defaultState = (0 until machine.targetState.size).map { false }
        println("Searching solution for ${machine.line}")
        val result = (1 until machine.buttons.size).firstNotNullOf { numberOfButtons ->
            val combinations = machine.buttons.combinations(numberOfButtons)
//            println("Generating combinations for $numberOfButtons: ${combinations}")
            val workingCombination = combinations.firstNotNullOfOrNull { combination ->
                val pressState = combination.fold(defaultState) { acc, button ->
                    acc.mapIndexed { i, v -> v xor button.state[i] }
                }
//                println("State for pressing $combination=$pressState")
                if (pressState == machine.targetState) combination else null
            }
            if (workingCombination != null) {
                println("Found working combination ${workingCombination.map { it.raw }} [$numberOfButtons]")
                workingCombination to numberOfButtons
            } else {
                null
            }
        }
        return result.second
    }

    fun <T> List<T>.combinations(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        return flatMapIndexed { index, element ->
            subList(index + 1, size)
                .combinations(k - 1)
                .map { listOf(element) + it }
        }
    }

    private data class Machine(
        val line: String,
        val targetState: List<Boolean>,
        val buttons: List<Button>,
    )

    private data class Button(
        val raw: String,
        val state: List<Boolean>
    )

    private fun parseMachines(lines: List<String>): List<Machine> {
        return lines.map { line ->
            val target = line.dropWhile { it != '[' }.drop(1).takeWhile { it != ']' }
                .map { c -> c == '#' }
            val buttons = line.dropWhile { it != ' ' }.takeWhile { it != '{' }.trim().split(" ")
                .map { btns ->
                    val indexes = btns.replace("(", "").replace(")", "")
                        .split(",").map { it.toInt() }
                    val state = (0 until target.size).map { i -> i in indexes }
                    Button(raw = btns, state = state)
                }
            Machine(
                line = line,
                targetState = target,
                buttons = buttons,
            )
        }
    }
}