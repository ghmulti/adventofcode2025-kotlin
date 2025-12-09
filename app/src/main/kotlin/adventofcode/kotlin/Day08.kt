package adventofcode.kotlin

import kotlin.math.sqrt

object Day08 {

    fun solve() {
        val filename = "day08.txt"
        val lines = javaClass.classLoader.getResource(filename)!!
            .readText().lines()
//        println(lines)

        val coords = lines.mapIndexed { index, line ->
            val parts = line.split(",")
            Coordinate(
                index = index,
                x = parts[0].toInt(),
                y = parts[1].toInt(),
                z = parts[2].toInt(),
            )
        }

        val defaultCircuits = coords.map { coord -> Circuit(coords = setOf(coord)) }

        val sortedUniquePairs = coords.combinations().sortedBy { it.distance }

        val pairs = sortedUniquePairs.take(10.takeIf { filename.contains("test") } ?: 1000)
        val circuits = pairs.fold(defaultCircuits.toMutableList()) { acc, pair ->
            val circuit1 = acc.find { it.coords.contains(pair.c1) }
            val circuit2 = acc.find { it.coords.contains(pair.c2) }
            if (circuit1 != circuit2) {
                acc.remove(circuit1)
                acc.remove(circuit2)
                acc.add(Circuit(coords = circuit1!!.coords + circuit2!!.coords))
            }
            acc
        }.sortedByDescending { it.coords.size }.take(3)
        val result1 = circuits.map { it.coords.size.toLong() }.reduce(Long::times)
        println("Result1: $result1")


        val mutatedCircuits = defaultCircuits.toMutableList()
        val iter = sortedUniquePairs.iterator()
        lateinit var nextPair: CoordinatePair
        while (mutatedCircuits.size > 1) {
            nextPair = iter.next()
            val circuit1 = mutatedCircuits.find { it.coords.contains(nextPair.c1) }
            val circuit2 = mutatedCircuits.find { it.coords.contains(nextPair.c2) }
            if (circuit1 != circuit2) {
                mutatedCircuits.remove(circuit1)
                mutatedCircuits.remove(circuit2)
                mutatedCircuits.add(Circuit(coords = circuit1!!.coords + circuit2!!.coords))
            }
        }
        println("Result2: ${nextPair.c1.x.toLong() * nextPair.c2.x.toLong()}")
    }

    private fun List<Coordinate>.combinations(): List<CoordinatePair> =
        flatMapIndexed { i, c1 ->
            subList(i + 1, size).map { c2 ->
                CoordinatePair(c1, c2, distance(c1, c2))
            }
        }

    private fun distance(c1: Coordinate, c2: Coordinate): Double {
        val dx = (c1.x - c2.x).toDouble()
        val dy = (c1.y - c2.y).toDouble()
        val dz = (c1.z - c2.z).toDouble()
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    private data class CoordinatePair(
        val c1: Coordinate,
        val c2: Coordinate,
        val distance: Double
    )

    private data class Coordinate(
        val index: Int,
        val x: Int,
        val y: Int,
        val z: Int,
    )

    private data class Circuit(
        val coords: Set<Coordinate>
    )
}