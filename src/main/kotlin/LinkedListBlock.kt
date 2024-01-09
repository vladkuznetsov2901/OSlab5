import java.io.*
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

class LinkedListBlock {
    var file: File? = null
    var fileWriter: FileWriter? = null
    var fileReader: BufferedReader? = null
    var cache: List<String>? = null
    var linkedListBlock = ArrayList<Node?>()
    private var sizeOfBlocks = 0
    @Throws(IOException::class)
    fun initListBlock(count: Int, pathToFile: String?) {
        if (count % 2 == 0 && (count > 1024 || count < 65.536)) {
            sizeOfBlocks = count
        } else {
            println("Введено некоректное значение для размера блока!!!")
            System.exit(500)
        }
        file = File(pathToFile)
        if (!file!!.exists()) {
            file!!.createNewFile()
        }
        fileWriter = FileWriter(file)
    }

    @Throws(IOException::class)
    fun addBlock(count: Int) {
        val reader = BufferedReader(FileReader(file))
        var lastStr: Array<String>?
        var lastIndexBlockInFile = 0
        val data = reader.lines().filter { word: String ->
            word.contains(
                "{"
            )
        }.toList()
        try {
//             lastStr = data.get(data.size() - 1).replaceAll("(^\\{\\d;|=\\d+|,\\d+|\\d+}|})|(=null)", "");
            lastStr =
                data[data.size - 1].replace("(^\\{|=\\d+|,\\d+|\\d+}|})|(=null)".toRegex(), "").split(";".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            lastIndexBlockInFile = lastStr[0].toInt() + lastStr[1].toInt()
        } catch (e: Exception) {
            lastStr = null
        }
        try {
            if (lastStr == null) {
                if (currentIndex >= linkedListBlock[linkedListBlock.size - 1]!!.lastIndexBlock) {
                    val node = Node(linkedListBlock[linkedListBlock.size - 1]!!.lastIndexBlock, count)
                    currentIndex = node.lastIndexBlock
                    linkedListBlock.add(node)
                }
            } else {
                currentIndex = lastIndexBlockInFile
                val node = Node(lastIndexBlockInFile, count)
                currentIndex = node.lastIndexBlock
                linkedListBlock.add(node)
            }
        } catch (`$e`: IndexOutOfBoundsException) {
            val node = Node(count)
            linkedListBlock.add(node)
            currentIndex = count
            currentStartIndex = node.startBLock
        }
        reader.close()
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    fun removeBlock(start: Int) {
        var nodeDeleted: Node? = null
        for (node in linkedListBlock) {
            if (start == node!!.startBLock) {
                nodeDeleted = node
                break
            }
        }
        linkedListBlock.remove(nodeDeleted)
        if (fileReader != null) {
            try {
                val reader = BufferedReader(FileReader(file))
                val dataFiles = ArrayList(reader.lines().toList())
                //                dataFiles.remove(0);
                val stingToDelete = fileReader!!.lines().filter { word: String ->
                    word.contains(
                        "{$start"
                    )
                }.findFirst().get()
                FileChannel.open(Paths.get(file!!.path), StandardOpenOption.WRITE).truncate(0).close()
                for (str in dataFiles) {
                    if (str != stingToDelete) {
                        fileWriter!!.write(str + "\n")
                    }
                }
                fileWriter!!.flush()
                reader.close()
            } catch (e: Exception) {
                println("\nНеправильно указан индекс начало цепочки блоков или данной цепочки не существует!")
            }
        }
    }

    @Throws(IOException::class)
    fun writeInfoToBlock(start: Int, info: String) {
        for (i in linkedListBlock) {
            if (start == i!!.startBLock) {
                val b = info.toByteArray(StandardCharsets.UTF_8)
                val a = b.contentToString().split("[\\[\\]]".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].replace(" ", "")
                if (b.size < i.countBlock * sizeOfBlocks) {
                    i.info = a
                    break
                } else {
                    println("В данной цепочке блоков недостаточно памяти для расположения этих данных!!!")
                    break
                }
            }
        }
        if (fileReader != null) {
            val reader = BufferedReader(FileReader(file))
            var stingWithInfo: String?
            try {
                stingWithInfo = fileReader!!.lines().filter { word: String ->
                    word.contains(
                        "{$start"
                    )
                }.findFirst().get()
            } catch (e: Exception) {
                stingWithInfo = null
                var i = 0
                while (i < linkedListBlock.size) {
                    if (linkedListBlock[i]!!.startBLock === start) {
                        stingWithInfo =
                            (("{" + linkedListBlock[i]!!.startBLock).toString() + ";" + linkedListBlock[i]!!.countBlock).toString() + "=null}"
                    }
                    i++
                }
            }
            if (stingWithInfo == null) {
                println("Цепочки с данным начальным индексом нет!")
                return
            }
            val indexes = stingWithInfo.replace("(^\\{|=\\d+|,\\d+|\\d+}|})|(=null)".toRegex(), "").split(";".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (stingWithInfo == null) {
                println("Цепочки с данным начальным индексом нет!")
            } else {
                val b = info.toByteArray(StandardCharsets.UTF_8)
                val a = b.contentToString().split("[\\[\\]]".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].replace(" ", "")
                Files.writeString(
                    file!!.toPath(), Files.readString(file!!.toPath())
                        .replace(stingWithInfo, "{" + indexes[0] + ";" + indexes[1] + "=" + a + "}")
                )
            }
        }
    }

    fun readInfoFromBlocks(start: Int): String? {
        for (i in linkedListBlock) {
            if (start == i!!.startBLock) {
                val info: Array<String> = i.info!!.split(",").toTypedArray()
                val b = toByteArrayDataBlockChain(info)
                return String(b, StandardCharsets.UTF_8)
            }
        }
        if (fileReader != null) {
            try {
                val info = fileReader!!.lines().filter { word: String ->
                    word.contains(
                        "{$start"
                    )
                }.findFirst().get()
                val finalInfo = info.replace("((^\\{\\d+;\\d+=)|})".toRegex(), "")
                val b = toByteArrayDataBlockChain(finalInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray())
                return String(b, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                println("\nНеправильно указан индекс начало цепочки блоков или данной цепочки не существует!")
            }
        }
        return null
    }

    fun toByteArrayDataBlockChain(info: Array<String>): ByteArray {
        val byteResult = ByteArray(info.size)
        for (j in info.indices) {
            byteResult[j] = info[j].toInt().toByte()
        }
        return byteResult
    }

    fun removeInfoFromBlock(start: Int) {
        for (i in linkedListBlock) {
            if (start == i!!.startBLock) {
                i.info = null
                break
            }
        }
    }

    @Throws(IOException::class)
    fun enterInBlockSpace(pathToFile: String?) {
        file = File(pathToFile)
        val cacheReader = BufferedReader(FileReader(file))
        cache = cacheReader.lines().toList()
        fileReader = BufferedReader(FileReader(file))
        sizeOfBlocks = fileReader!!.readLine().toInt()
        fileWriter = FileWriter(file, true)
        cacheReader.close()
    }

    @Throws(IOException::class)
    fun printInfoLinkedList() {
        var countEmptyBlockInManager = 0
        var countEmptyBlockInFile = 0
        var countFuelBlockInFile = 0
        for (i in linkedListBlock) {
            if (i!!.info == null) {
                countEmptyBlockInManager++
            }
        }
        if (fileReader != null) {
            val reader = BufferedReader(FileReader(file))
            val information = ArrayList(reader.lines().toList())
            information.removeAt(0)
            for (str in information) {
                if (str.contains("null")) {
                    countEmptyBlockInFile++
                } else countFuelBlockInFile++
            }
            reader.close()
        }
        println("Размер одного блока: $sizeOfBlocks")
        println("Размер linkedListBlock: " + linkedListBlock.size)
        println("Количество пустых блоков: " + (countEmptyBlockInManager + countEmptyBlockInFile))
        println(
            "Количество не пустых блоков: " +
                    (linkedListBlock.size - countEmptyBlockInManager + countFuelBlockInFile)
        )
        if (cache != null) {
            println("Размер кэша: " + cache!!.size)
        }
    }

    @Throws(IOException::class)
    fun writeListToFile() {
        if (fileReader == null) {
            fileWriter!!.write(sizeOfBlocks.toString() + "\n")
        }
        for (i in linkedListBlock.indices) {
            fileWriter!!.write(((("{" + linkedListBlock[i]!!.startBLock).toString() + ";" + linkedListBlock[i]!!.countBlock).toString() + "=" + linkedListBlock[i]!!.info).toString() + "}\n")
            fileWriter!!.flush()
        }
        fileWriter!!.flush()
        fileWriter!!.close()
        if (fileReader != null) {
            fileReader!!.close()
        }
    }

    @Throws(IOException::class)
    fun writeListToFileRecordWithoutChanges() {
        val writer = PrintWriter(file)
        for (str in cache!!) {
            writer.println(str)
        }
        writer.flush()
        writer.close()
        fileWriter!!.close()
        fileReader!!.close()
    }

    companion object {
        var currentIndex = 0
        private var currentStartIndex = 0
    }
}
