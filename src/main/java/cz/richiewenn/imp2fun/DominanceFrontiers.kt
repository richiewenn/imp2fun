package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.HashSet

object DominanceFrontiers {
    fun calculate(node: Node): Set<Node> {
        val dominanceFrontiers = HashSet<Node>()
        deepSearch(node) { b ->
            if (b.inEdges.size >= 2) {
//                for (p in b.parents()) {
//                    val runner = p
                    //                    while (!b.doms.contains(runner)) {
//                        dominanceFrontiers.add(b)
//                        runner.dominanceFrontiers.add(b)
//                    }
                    fun searchDF(runner: Node) {
                        if (b.idom() != runner) {
                            runner.parents().forEach { searchDF(it) }
                        }
                        if(b.idom() == runner) {
                            dominanceFrontiers.add(b)
                        }
                    }
                    searchDF(b)
//                }
            }
        }
        return dominanceFrontiers
    }
}

fun deepSearch(root: Node, callback: (Node) -> Unit) {
    val stack: Stack<Node> = Stack()
    fun f(node: Node) {
        stack.add(node)
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }
            if (!stack.contains(edge.node)) {
                f(edge.node!!)
            }
            callback(node)
        }
    }
    f(root)
}

