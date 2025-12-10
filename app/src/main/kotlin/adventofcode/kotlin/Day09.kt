package adventofcode.kotlin

import kotlin.collections.flatMap
import kotlin.math.abs

object Day09 {

    fun solve() {
        val filename = "day09.txt"
        val lines = javaClass.classLoader.getResource(filename)!!
            .readText().lines().filter { it.isNotBlank() }

        val coords = lines.map { line ->
            val parts = line.split(",")
            Coordinate(x = parts[0].toInt(), y = parts[1].toInt())
        }

        val max = coords.combinations().maxByOrNull { it.area }
        println("Result1: $max")

        val rectangles = coords.combinations()
            .sortedByDescending { it.area }
            .map { pair ->
                val rectCoords = listOf(
                    pair.c1,
                    Coordinate(x = pair.c1.x, y = pair.c2.y),
                    pair.c2,
                    Coordinate(x = pair.c2.x, y = pair.c1.y)
                )
                Rectangle(
                    coords = rectCoords,
                    area = pair.area
                )
            }

        val polygonBoundaries = polygonBoundaryPoints(coords)
//        visualize(polygonBoundaries)
        val outsideCoords = outsideCoordinates(polygon = coords, boundaries = polygonBoundaries)
//        visualize(outsideCoords)

        val result = rectangles.firstOrNull { rect ->
            val boundsElements = polygonBoundaryPoints(rect.coords)
//            println("Working with rect=$rect [elements=${boundsElements.size}]")
            boundsElements.all { coord -> coord !in outsideCoords }
        }
        println("Result2: ${result?.area}")
    }

    private val fillDirections = listOf(
        Coordinate(1, 0),
        Coordinate(-1, 0),
        Coordinate(0, 1),
        Coordinate(0, -1),
        Coordinate(-1, -1),
        Coordinate(1, 1),
        Coordinate(-1, 1),
        Coordinate(1, -1),
    )

    private fun outsideCoordinates(polygon: List<Coordinate>, boundaries: Set<Coordinate>): Set<Coordinate> {
        val minX = polygon.minOf { it.x } - 1
        val maxX = polygon.maxOf { it.x } + 1
        val minY = polygon.minOf { it.y } - 1
        val maxY = polygon.maxOf { it.y } + 1

        val start = boundaries.minByOrNull { it.y }?.let { minC -> Coordinate(minC.x, minC.y-1) } ?: error("??")
        val outside = mutableSetOf(start)
        val queue = ArrayDeque<Coordinate>().apply { add(start) }
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            val els = fillDirections.map { d ->
                val nx = p.x + d.x
                val ny = p.y + d.y
                if (nx !in minX..maxX || ny !in minY..maxY) return@map null to false
                val np = Coordinate(nx, ny)
                if (np in outside) return@map null to false
                if (np in boundaries) return@map null to true
                np to true
            }
            // require one neighbour
            if (els.none { it.first == null && it.second }) {
                continue
            }
            val elements = els.mapNotNull { it.first }
            outside.addAll(elements)
            queue.addAll(elements)
        }
        return outside
    }

    private data class Coordinate(val x: Int, val y: Int)

    private data class Rectangle(
        val coords: List<Coordinate>,
        val area: Long,
    )

    private data class CoordinatePair(
        val c1: Coordinate,
        val c2: Coordinate,
        val area: Long
    )

    private fun List<Coordinate>.combinations(): List<CoordinatePair> =
        flatMapIndexed { i, c1 ->
            subList(i + 1, size).map { c2 ->
                CoordinatePair(
                    c1 = c1,
                    c2 = c2,
                    area = area(c1, c2)
                )
            }
        }

    private fun area(a: Coordinate, b: Coordinate): Long {
        return (abs(a.x - b.x) + 1L) * (abs(a.y - b.y) + 1L)
    }

    private fun polygonBoundaryPoints(polygon: List<Coordinate>): Set<Coordinate> {
        return polygon.indices.flatMap { i ->
            val a = polygon[i]
            val b = polygon[(i + 1) % polygon.size]
            when {
                a.x == b.x -> {
                    val fromY = minOf(a.y, b.y)
                    val toY = maxOf(a.y, b.y)
                    (fromY..toY).map { Coordinate(x = a.x, y = it) }
                }
                a.y == b.y -> {
                    val fromX = minOf(a.x, b.x)
                    val toX = maxOf(a.x, b.x)
                    (fromX..toX).map { Coordinate(x = it, y = a.y) }
                }
                else -> error("should not happen")
            }
        }.toSet()
    }

    private fun visualize(coords: Collection<Coordinate>) {
        (0..coords.maxOf { it.y }).forEach { row ->
            val line = (0..coords.maxOf { it.x }).joinToString("") { col ->
                if (coords.contains(Coordinate(col, row))) {
                    "X"
                } else {
                    "."
                }
            }
            println(line)
        }
    }
}