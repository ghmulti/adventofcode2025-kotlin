package adventofcode.kotlin

import kotlin.collections.mapIndexed


object Day10 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day10-test.txt")!!
            .readText().lines().filter { it.isNotBlank() }
//        println(lines)

        val machines = parseMachines(lines)
        println(machines.first())

        val minNumberOfPresses = machines.sumOf(::findMinNumberOfButtonPresses)
        println("Result1: $minNumberOfPresses")

        val minNumberOfPressesForJoltage = machines.sumOf(::findMinNumberOfButtonPressesForJoltage)
        println("Result2: $minNumberOfPressesForJoltage")
    }

    private fun findMinNumberOfButtonPressesForJoltage(machine: Machine): Int {
        println("Searching solution for ${machine.line}")
        val buttonsList = bfs(machine)
        println("Found buttons[${buttonsList.size}]: ${buttonsList.map { it.raw }}")
        return buttonsList.size
    }

    private fun bfs(machine: Machine): List<Button> {
        val start = machine.buttons.map { button ->
                button to machine.joltage.decreaseWith(button.stateNum)
            }.filter { (_, state) -> state.all { it >= 0 } }
            .minByOrNull { (_, state) -> state.sum() } ?: error("No joltage found")
        println("Start element $start")

        val queue = ArrayDeque<ButtonColl>().apply { add(ButtonColl(buttons = listOf(start.first))) }
        while (queue.isNotEmpty()) {
            val buttonPresses = queue.removeFirst()
            val state = buttonPresses.combine()
            val results = machine.buttons.map { button ->
                val stateWithButton = state.withButton(button.stateNum)
                button to machine.joltage.decreaseWith(stateWithButton)
            }.filter { (_, state) -> state.all { it >= 0 } }.sortedBy { (_, state) -> state.sum() }

            results.firstOrNull { (_, s) -> s.sum() == 0 }?.let { (button, _) ->
                return buttonPresses.buttons + button
            }

            val children = results.map { (button, _) ->
                ButtonColl(buttons = buttonPresses.buttons + button)
            }
            queue.addAll(children)
        }
        error("Unable to find")
    }

    private data class ButtonColl(
        val buttons: List<Button>,
    ) {
        fun combine(): List<Int> {
            val defaultState = (0 until buttons.first().stateNum.size).map { 0 }
            return buttons.fold(defaultState) { acc, button ->
                acc.mapIndexed { i, v -> v + button.stateNum[i] }
            }
        }
    }

    private fun List<Int>.withButton(a: List<Int>): List<Int> {
        return this.mapIndexed { i, v -> v + a[i] }
    }

    private fun List<Int>.decreaseWith(a: List<Int>): List<Int> {
        return this.mapIndexed { i, v -> v - a[i] }
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
    )

    private data class Button(
        val raw: String,
        val state: List<Boolean>,
        val stateNum: List<Int>
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