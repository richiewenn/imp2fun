package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.JumpExpr
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DominatorTreeTransformer {
    private var stack: Stack<Node> = Stack()
    private var markers: MutableMap<Int, Int> = HashMap()

    // O(N*M)
    // 1. DFS graph and mark nodes
    // 2. remove node 'w'
    // 3. DFS graph again, any unreachable node 'u' is dominated by 'w'

    private fun firstRun(node: Node): Node {
        this.stack.add(node)
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }

            if (!this.stack.contains(edge.node)) {
                val value = this.markers[edge.node?.id]
                if(value != null) {
                    this.markers.put(edge.node!!.id, value+1)
                } else {
                    this.markers.put(edge.node!!.id, 1)
                }
                this.firstRun(edge.node!!)
            }
        }
        return node
    }

    private fun secondRun(node: Node): Node {
        this.stack.add(node)
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }

            if (!this.stack.contains(edge.node)) {
                val value = this.markers[edge.node?.id]
                if(value != null) {
                    this.markers.put(edge.node!!.id, value+1)
                } else {
                    this.markers.put(edge.node!!.id, 1)
                }
                this.firstRun(edge.node!!)
            }
        }
        return node
    }

    fun transform(node: Node) {
        val firstRun = firstRun(node)
        val node = firstRun.outEdges[0].node!!.outEdges[0].node!!.outEdges[0].node
        firstRun.outEdges[0].node!!.outEdges[0].node!!.outEdges = emptyList()
        this.stack = Stack()
        val secondRun = secondRun(firstRun)
        val dominated = this.markers
            .filter { it.value == 1 }
            .map { it.key }
            .toList()
        println("Dominator: ${node!!.id}")
        println("Dominated: $dominated")
    }
}

class TreeNode<T> (
    val value: T,
    val nodes: List<TreeNode<T>> = emptyList()
)