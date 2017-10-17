package cz.richiewenn.imp2fun.cfg

class Node(var outEdges: List<Edge> = emptyList()) {
    constructor(vararg edges: Edge) : this(edges.toList())
    var inEdges: List<Edge> = ArrayList()

    companion object { var lastId = 0 }
    val id = lastId++

    fun plusLeft(node: Node): Node {
        if(this.lastLeft().outEdges.isEmpty()) {
            this.lastLeft().outEdges = node.outEdges
        } else {
            this.lastLeft().outEdges.first().nodes = listOf(node)
        }
        return this
    }

    fun lastLeft(): Node {
        var last: Node = this
        while(last.outEdges.isNotEmpty() && last.outEdges.first().nodes.isNotEmpty()) {
            last = last.outEdges.first().nodes.first()
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