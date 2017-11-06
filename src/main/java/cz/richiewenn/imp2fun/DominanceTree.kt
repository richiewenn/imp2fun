package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.HashSet

class DominanceTree {
    var stack: Stack<Node> = Stack()
    var idoms = HashSet<Pair<Int, Int>>()
    var doms = HashSet<Pair<Int, Set<Int>>>()


    fun dominanceTree(node: Node) {
        this.stack.add(node)
        if(node.inEdges.size == 1) {
            idoms.add(Pair(node.inEdges.first().node!!.id, node.id))
        }
            for (edge in node.outEdges) {
                if (edge.node == null) {
                    continue
                }

                if (!this.stack.contains(edge.node)) {
                    this.dominanceTree(edge.node!!)
                }
            }

    }
}
