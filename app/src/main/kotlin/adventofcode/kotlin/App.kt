package adventofcode.kotlin

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(App().greeting)
    Day01.solve()
    Day02.solve()
}
