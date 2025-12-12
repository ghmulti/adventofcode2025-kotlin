package adventofcode.kotlin

object Day12 {

    fun solve() {
        val lines = javaClass.classLoader.getResource("day12.txt")!!
            .readText().lines()
//        println(lines)

        val world = parseInput(lines)
//        println(world)

        // Exact Cover + Algorithm X / DLX: TODO LOL :)
        val fits = world.shapes.map { shape ->
            val shapeArea = shape.width * shape.length
            val figureShapes = shape.figureRefs.sum() * 9
            if (shapeArea < figureShapes) 0 else 1
        }
        println("Result: ${fits.sum()}")
    }


    private fun parseInput(lines: List<String>): World {
        val figures = lines.take(6*5)
            .windowed(5, 5)
            .map { sub ->
                val index = sub[0].take(1).toInt()
                val rows = sub.drop(1).take(3).map { l -> listOf(l[0], l[1], l[2]) }
                Figure(rows = rows, index = index)
            }

        val shapes = lines.drop(6*5).filter { it.isNotBlank() }.map { line ->
            val parts = line.split(":")
            val dim = parts[0].split("x")
            Shape(
                width = dim[0].toInt(),
                length = dim[1].toInt(),
                figureRefs = parts[1].trim().split(" ").map { it.trim().toInt() }
            )
        }

        return World(
            figures = figures,
            shapes = shapes,
        )
    }
}

private data class World(
    val figures: List<Figure>,
    val shapes: List<Shape>,
)

private data class Figure(
    val index: Int,
    val rows: List<List<Char>>
)

private data class Shape(
    val width: Int,
    val length: Int,
    val figureRefs: List<Int>,
)