package cz.richiewenn.imp2fun.cfg

class Node(var outEdges: List<Edge> = emptyList()) {
    constructor(vararg edges: Edge) : this(edges.toList())
    var inEdges: MutableList<Edge> = ArrayList()

    companion object { var lastId = 0 }
    val id = lastId++

    fun plusLeft(node: Node): Node {
        if(this.lastLeft().outEdges.isEmpty()) {
            this.lastLeft().outEdges = node.outEdges
        } else {
            this.lastLeft().outEdges.first().node = node
        }
        return this
    }

    fun lastLeft(): Node {
        var last: Node = this
        while(last.outEdges.isNotEmpty() && last.outEdges.first().node != null) {
            last = last.outEdges.first().node!!
        }
        return last
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}