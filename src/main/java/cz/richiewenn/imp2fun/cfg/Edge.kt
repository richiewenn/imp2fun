package cz.richiewenn.imp2fun.cfg

// TODO: Get rid of this nullable type Node?
class Edge(var node: Node? = null, val exp: String = "") {

    companion object { var lastId = 0 }
    val id = lastId++

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}