package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.JumpExpr
import java.util.*

/**
 * For optimizing JUMP edges in graphs.
 */
class CfgJumpOptimizer {
    private val stack: Stack<Node> = Stack()

    /**
     * It optimizes JUMP edges in graph.
     * It guaranties that there will be no JUMP edges in returned graph when and only when the every Node which
     * has a JUMP edge has only one outgoing edge and that is the JUMP.
     * @param root graph to be optimized.
     * @return same graph just without JUMP edges
     */
    fun optimize(node: Node): Node {
        this.stack.add(node)
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }

            if (!this.stack.contains(edge.node)) {
                this.optimize(edge.node!!)
            }

            if (edge.exp is JumpExpr) {
                val child = node.outEdges.first().node!!
                node.inEdges.forEach {
                    it.node?.outEdges
                        ?.filter { parentEdges -> parentEdges.node?.id == node.id }
                        ?.forEach { edge -> edge.node = child }
                }
                val edgs = child.inEdges.filter { e -> e.node?.id != node.id}
                child.inEdges = (edgs + node.inEdges).toMutableSet()
            }
        }
        return node
    }
}