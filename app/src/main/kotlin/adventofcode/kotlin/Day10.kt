package adventofcode.kotlin

import kotlin.collections.mapIndexed
import kotlin.collections.plus


object Day10 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day10-test.txt")!!
            .readText().lines().filter { it.isNotBlank() }
//        println(lines)

        val machines = parseMachines(lines)
        println(machines.first())

        val minNumberOfPresses = machines.sumOf(::findMinNumberOfButtonPresses)
        println("Result1: $minNumberOfPresses")

        // https://www.reddit.com/r/adventofcode/comments/1pk87hl/2025_day_10_part_2_bifurcate_your_way_to_victory/
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
        val joltage: List<Int>,
    ) {
        override fun toString(): String {
            return "Machine: $buttons $joltage"
        }
    }

    private data class Button(
        val raw: String,
        val state: List<Boolean>,
        val stateNum: List<Int>
    ) {
        override fun toString(): String {
            return raw
        }
    }

    private fun parseMachines(lines: List<String>): List<Machine> {
        return lines.map { line ->
            val target = line.dropWhile { it != '[' }.drop(1).takeWhile { it != ']' }
                .map { c -> c == '#' }
            val buttons = line.dropWhile { it != ' ' }.takeWhile { it != '{' }.trim().split(" ")
                .map { btns ->
                    val indexes = btns.replace("(", "").replace(")", "")
                        .split(",").map { it.toInt() }
                    val state = (0 until target.size).map { i -> i in indexes }
                    val stateNum = state.map { if (it) 1 else 0 }
                    Button(raw = btns, state = state, stateNum = stateNum)
                }
            val joltage = line.dropWhile { it != '{' }.drop(1).takeWhile { it != '}' }
                .split(",").map { it.toInt() }
            Machine(
                line = line,
                targetState = target,
                buttons = buttons,
                joltage = joltage,
            )
        }
    }
}