package adventofcode.kotlin

import kotlin.collections.mapIndexed
import kotlin.collections.plus


object Day10 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day10.txt")!!
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
        val start = machine.buttons.maxByOrNull { it.stateNum.sum() } ?: error("???")
        println("Start element $start")

        val defaultState = StateHolder(
            pressed = listOf(start),
            buttonsLeft = machine.buttons,
        )
        val queue = ArrayDeque<StateHolder>().apply { add(defaultState) }
        while (queue.isNotEmpty()) {
            val stateHolder = queue.removeFirst()

            val buttonVariants: List<List<Button>> = stateHolder.buttonsLeft.flatMap { button ->
                buttonMultiplier(button = button, stateHolder = stateHolder, joltage = machine.joltage)
            }.sortedWith(compareBy<List<Button>> { it.size }.thenByDescending { combinePressed(it).sum() })

            val solution = findSolution(variants = buttonVariants, joltage = machine.joltage, stateHolder = stateHolder)
            if (solution != null) {
                return solution
            }

            val children = buttonVariants.map { sameButtons ->
                StateHolder(
                    pressed = stateHolder.pressed + sameButtons,
                    buttonsLeft = stateHolder.buttonsLeft.filter { it != sameButtons.first() },
                )
            }
            queue.addAll(children)
        }
        error("Unable to find")
    }

    private fun findSolution(variants: List<List<Button>>, stateHolder: StateHolder, joltage: List<Int>): List<Button>? {
        val pressed = combinePressed(stateHolder.pressed)
        val joltageWithoutPressed = joltage.decreaseWith(pressed)
        val result = variants.firstOrNull { buttons ->
            val newPressed = combinePressed(buttons)
            val totalPressed = joltageWithoutPressed.decreaseWith(newPressed)
            totalPressed.sum() == 0
        }
        return if (result != null) {
            stateHolder.pressed + result
        } else {
            null
        }
    }

    private fun buttonMultiplier(button: Button, stateHolder: StateHolder, joltage: List<Int>): List<List<Button>> {
        val combined = combinePressed(stateHolder.pressed)
        val joltageWithoutPressed = joltage.decreaseWith(combined)
        val result = generateSequence(
            seed = emptyList<Button>()
        ) { buttons ->
            val addB = buttons + listOf(button)
            val combinedAddB = combinePressed(addB)
            val joltageState = joltageWithoutPressed.decreaseWith(combinedAddB)
            if (joltageState.all { it >= 0 }) addB else null
        }.drop(1).toList()
        return result
    }

    private data class StateHolder(
        val buttonsLeft: List<Button>,
        val pressed: List<Button>,
    )

    private fun combinePressed(pressed: List<Button>): List<Int> {
        val defaultState = (0 until pressed.first().stateNum.size).map { 0 }
        return pressed.fold(defaultState) { acc, button ->
            acc.mapIndexed { i, v -> v + button.stateNum[i] }
        }
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