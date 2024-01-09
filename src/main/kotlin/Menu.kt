import java.io.IOException
import java.util.*

class Menu {
    private var linkedListBlock: LinkedListBlock? = null
    @Throws(IOException::class)
    fun printMenu() {
        val selection: Int
        val input = Scanner(System.`in`)
        println("Выберите вариант")
        println("-------------------------\n")
        println("1 - Инициализация блочного пространства")
        println("2 - Выделение заданного количества пустых блоков")
        println("3 - Удалить выделеные ранее блоки")
        println("4 - Запись данных в указанную цепочку блоков")
        println("5 - Чтение данных из указанной цепочки блоков")
        println("6 - Справка")
        println("7 - Начать сессию в уже инициализированном пространстве")
        println("8 - Завершить текущую сессию")
        println("9 - Откат транзакции")
        selection = input.nextLine().toInt()
        var path: String
        when (selection) {
            1 -> {
                println("Введите размер блока:")
                val size = input.nextLine().toInt()
                print("Укажите путь к файлу абстрактного блочного дискового пространства: ")
                val path = input.nextLine()
                linkedListBlock = LinkedListBlock()
                linkedListBlock!!.initListBlock(size, path)
            }

            2 -> if (!isNotWork) {
                println("Выберете количество выделенных пустых блоков в цепочке")
                val count = input.nextLine().toInt()
                linkedListBlock!!.addBlock(count)
            }

            3 -> if (!isNotWork) {
                println("Выберете индекс начального блока в цепочке:")
                val index = input.nextLine().toInt()
                linkedListBlock!!.removeBlock(index)
            }

            4 -> if (!isNotWork) {
                println("Выберете индекс начального блока в цепочке:")
                val index = input.nextLine().toInt()
                println("Введите данное которые хотите сюда поместить:")
                val info = input.nextLine()
                linkedListBlock!!.writeInfoToBlock(index, info)
            }

            5 -> if (!isNotWork) {
                println("Выберете индекс начального блока в цепочке:")
                val index = input.nextLine().toInt()
                val info = linkedListBlock!!.readInfoFromBlocks(index)
                if (info != null) {
                    println(info)
                } else {
                    println("Неправильно указан индекс начало цепочки блоков или данной цепочки не существует!")
                }
            }

            6 -> if (!isNotWork) {
                linkedListBlock!!.printInfoLinkedList()
            }

            7 -> if (isNotWork) {
                print("Укажите путь к файлу абстрактного блочного дискового пространства: ")
                path = input.nextLine()
                linkedListBlock = LinkedListBlock()
                linkedListBlock!!.enterInBlockSpace(path)
            }

            8 -> if (!isNotWork) {
                linkedListBlock!!.writeListToFile()
                linkedListBlock = null
                System.exit(200)
            }

            9 -> if (!isNotWork) {
                try {
                    linkedListBlock!!.writeListToFileRecordWithoutChanges()
                    linkedListBlock = null
                    System.exit(200)
                } catch (e: Exception) {
                    System.exit(500)
                }
            }

            else -> println("Выберите что-нибудь другое!")
        }
    }

    val isNotWork: Boolean
        get() {
            val result = linkedListBlock == null
            if (result) {
                println("Вы сейчас не в блочном простаранстве!")
            }
            return result
        }
}
