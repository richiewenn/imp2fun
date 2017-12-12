package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node

class DominanceTree {
    fun dominanceTree(node: Node): Set<Pair<Int, Int>> {
        remarkIds(node)
        node.resetColors()
        this.run(node)
        node.resetColors()
        return this.mapIdoms(node)
    }

    private fun mapIdoms(node: Node): Set<Pair<Int, Int>> {
        val result = HashSet<Pair<Int, Int>>()
        fun f(node: Node) {
            if (node.color == Node.Color.GREY) {
                return
            }
            node.color = Node.Color.GREY

            val idom = node.doms.filter { it.id != node.id }.minBy { it.id }
            if(idom != null) {
                result.add(Pair(idom.id, node.id))
            }

            node.children().forEach { f(it) }
            node.color = Node.Color.BLACK
        }
        f(node)
        return result
    }

    private fun run(node: Node) {
        if (node.color == Node.Color.GREY) {
            return
        }
        node.color = Node.Color.GREY
        val doms = node.fwParents().map { it.doms }
        if (doms.isEmpty()) {
            node.doms = setOf(node)
        } else {
            node.doms = doms.reduce({ acc, d -> acc.intersect(d) }) + node
        }

        node.children().forEach { run(it) }
        node.color = Node.Color.BLACK
    }
}

fun remarkIds(node: Node) {
    var index = 0
    fun remark(node: Node) {
        node.color = Node.Color.GREY
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }
            if (edge.node?.color == Node.Color.GREY) {
                continue
            }
            remark(edge.node!!)
        }
        node.color = Node.Color.BLACK
        node.id = index
        index++
    }
    remark(node)
}
