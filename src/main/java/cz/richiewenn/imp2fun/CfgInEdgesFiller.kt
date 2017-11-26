package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import java.util.*

class CfgInEdgesFiller {
    private val stack: Stack<Node> = Stack()
    private val edges = HashMap<Node, Set<Edge>>() // to, from

    fun fill(node: Node): Node {
        node.resetColors()
        this.convert(node)
        this.edges.forEach { node, parents -> node.inEdges = parents.toMutableSet() }
        node.resetColors()
//        this.convert(node).forEach {
//            val parent = it.first
//            val child = it.second
//            val edge = it.third
//            child.inEdges.add(Edge(parent, edge.exp))
//        }
//        return node
        return node
    }

    private fun convert(node: Node) {
        if(node.color == Node.Color.GREY) {
            return
        }
        node.color = Node.Color.GREY
        node.children().forEach {
            val value = this.edges[it]
            val orientation = if(it.color == Node.Color.GREY) Edge.Orientation.BACKWARD else Edge.Orientation.FORWARD
            if(value == null) {
                this.edges[it] = setOf(Edge(node, orientation = orientation))
            } else {
                this.edges[it] = value + Edge(node, orientation = orientation)
            }
            this.convert(it)
        }
        node.color = Node.Color.BLACK
//        if(node == null) {
//            return emptyList()
//        }
//        this.stack.add(node)
//        val result = ArrayList<Triple<Node, Node, Edge>>()
//        for (edge in node.outEdges) {
//            if(edge.node == null) {
//                continue
//            }
//            result.addAll(
//                if (this.stack.contains(edge.node)) {
//                    listOf(Triple(node, edge.node!!, edge))
//                } else {
//                    listOf(Triple(node, edge.node!!, edge)).plus(this.convert(edge.node))
//                }
//            )
//        }
//        return result
    }
}