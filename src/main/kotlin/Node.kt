class Node {
    var startBLock: Int
    var lastIndexBlock: Int
    var countBlock: Int
    var info: String? = null

    constructor(count: Int) {
        startBLock = 1
        countBlock = count
        lastIndexBlock = count
    }

    constructor(start: Int, count: Int) {
        startBLock = start + 1
        countBlock = count
        lastIndexBlock = startBLock + count - 1
    }
}
