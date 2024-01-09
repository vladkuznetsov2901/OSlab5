import java.io.IOException

object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val menu = Menu()
        while (true) {
            menu.printMenu()
        }
    }
}
