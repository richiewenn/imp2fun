package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import java.util.*

class CfgInEdgesFiller {
    private val stack: Stack<Node> = Stack()

    fun fill(node: Node): Node {
        convert(node).forEach {
            val parent = it.first
            val child = it.second
            val edge = it.third
            child.inEdges.add(edge)
        }
        return node
    }

    private fun convert(node: Node?): List<Triple<Node, Node, Edge>> {
        if(node == null) {
            return emptyList()
        }
        this.stack.add(node)
        val result = ArrayList<Triple<Node, Node, Edge>>()
        for (edge in node.outEdges) {
            if(edge.node == null) {
                continue
            }
            result.addAll(
                if (this.stack.contains(edge.node)) {
                    listOf(Triple(node, edge.node!!, edge))
                } else {
                    listOf(Triple(node, edge.node!!, edge)).plus(this.convert(edge.node))
                }
            )
        }
        return result
    }
}