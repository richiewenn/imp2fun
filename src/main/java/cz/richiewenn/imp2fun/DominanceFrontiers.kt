package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.HashSet

object DominanceFrontiers {
    fun calculate(node: Node): Set<Node> {
        val dominanceFrontiers = HashSet<Node>()
        depthFirstSearch(node) { b ->
            if (b.inEdges.size >= 2) {
                val walk = ArrayList<Node>()
                fun searchDF(runner: Node) {
                    if (b.idom() != runner && !walk.contains(runner)) {
                        walk.add(runner)
                        runner.parents().forEach { searchDF(it) }
                    }
                    if (b.idom() == runner) {
                        dominanceFrontiers.add(b)
//                        walk.forEach { it.dominanceFrontiers.add(b) }
                    }
                }
                searchDF(b)
            }
        }
        return dominanceFrontiers
    }

    fun fill(node: Node): Node {
        remarkIds(node)
        val iHaveBeenThere = HashSet<Int>()
        depthFirstSearch(node) { b ->
            fun searchDF(runner: Node) {
                if(!b.doms.contains(runner)) {
                    if(runner == b) {
                        return
                    }
                    runner.dominanceFrontiers.add(b)
                    runner.fwParents().forEach { searchDF(it) }
                }
            }
            if (b.inEdges.size >= 2) {
                for (p in b.parents()) {
                    if(!iHaveBeenThere.contains(p.id)) {
                        iHaveBeenThere.add(p.id)
                        searchDF(p)
                    }
                }
            }
        }
        return node
    }
}

fun depthFirstSearch(root: Node, callback: (node: Node) -> Unit) {
    val stack: Stack<Node> = Stack()
    fun f(node: Node) {
        stack.add(node)
        for (childNode in node.children()) {
            if (!stack.contains(childNode)) {
                f(childNode)
            }
            callback(childNode)
        }
    }
    f(root)
    callback(root)
}

