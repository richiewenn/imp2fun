package cz.richiewenn.imp2fun.cfg

class Node(var edges: List<Edge> = emptyList()) {
    constructor(vararg edges: Edge) : this(edges.toList())

    companion object { var lastId = 0 }
    val id = lastId++

    fun plusLeft(node: Node): Node {
        if(this.lastLeft().edges.isEmpty()) {
            this.lastLeft().edges = node.edges
        } else {
            this.lastLeft().edges.first().nodes = listOf(node)
        }
        return this
    }

    fun lastLeft(): Node {
        var last: Node = this
        while(last.edges.isNotEmpty() && last.edges.first().nodes.isNotEmpty()) {
            last = last.edges.first().nodes.first()
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