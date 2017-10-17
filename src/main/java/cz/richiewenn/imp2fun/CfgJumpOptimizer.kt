package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
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
            edge.nodes.forEach {
                if (!this.stack.contains(it)) {
                    this.optimize(it)
                }
            }
            if(edge.exp == "JUMP") {
//                node.outEdges =
            }
        }
        return node
    }
}