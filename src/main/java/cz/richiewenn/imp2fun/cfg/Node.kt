package cz.richiewenn.imp2fun.cfg

open class Node(var edge: Edge) : Dot {
    companion object { var lastId = 0 }

    val id = lastId++
    fun plus(node: Node): Node {
        this.last().edge = node.edge
        return this
    }

    fun last(): Node {
        var last: Node? = this
        while(last?.edge?.expression != "END") {
            last = last?.edge?.to
        }
        return last
    }

    override fun toDot(label: Boolean): String = "$id"+edge.toDot()

    override fun toString(): String {
        return "[$id]"+edge.toString()
    }
}

class EndNode(expression: String = "END") : Node(EndEdge(expression))

class ConditionNode(var inner: Edge, otherwise: Edge) : Node(otherwise) {
    init {
        inner.expression = "IF "+inner.expression
    }

    override fun toDot(label: Boolean): String = "$id"+inner.toDot(label)+System.lineSeparator()+super.toDot(label)

    override fun toString(): String {
        return "[$id]IF"+inner.toString()+System.lineSeparator()+super.toString()
    }
}