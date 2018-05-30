package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node

data class DTNode(
    val node: Node,
    val children: List<DTNode>
) {
    /** Pair<Node, Depth from this node> */
    fun succ(): List<Pair<DTNode, Int>> {
        var depth = 0
        fun f(result: List<Pair<DTNode, Int>>, nodes: List<DTNode>): List<Pair<DTNode, Int>> {
            depth++
            val children = nodes.flatMap { it.children }
            if(children.isEmpty()) {
                return result
            } else {
                return result + children.map { Pair(it, depth) } + f(ArrayList(), children)
            }
        }
        return f(ArrayList(), listOf(this))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DTNode) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        var lastId = 0
    }

    val id = lastId++

}

class DominatorTree {
    fun dominanceTree(node: Node): Set<Pair<Int, Int>> {
        remarkIds(node)
        node.resetColors()
        this.run(node)
        node.resetColors()
        return this.mapIdoms(node)
    }

    fun dominanceNodeTree(node: Node): DTNode {
        remarkIds(node)
        node.resetColors()
        this.run(node)
        node.resetColors()
        val pairs = this.mapIdomsNodes(node)

        val root = pairs.map { it.first }.find { !pairs.map { it.second }.contains(it) }!!

        fun findChildren(node: Node): List<DTNode> = pairs
            .filter { it.first == node }
            .map { (_, child) ->
                DTNode(
                    node = child,
                    children = findChildren(child)
                )
            }

        return DTNode(
            node = root,
            children = findChildren(root)
        )
    }

    private fun mapIdoms(node: Node): Set<Pair<Int, Int>> {
        val result = HashSet<Pair<Int, Int>>()
        fun f(node: Node) {
            if (node.color == Node.Color.GREY) {
                return
            }
            node.color = Node.Color.GREY

            val idom = node.doms.filter { it.id != node.id }.minBy { it.id }
            if (idom != null) {
                result.add(Pair(idom.id, node.id))
            }

            node.children().forEach { f(it) }
            node.color = Node.Color.BLACK
        }
        f(node)
        return result
    }

    private fun mapIdomsNodes(node: Node): Set<Pair<Node, Node>> {
        val result = HashSet<Pair<Node, Node>>()
        fun f(node: Node) {
            if (node.color == Node.Color.GREY) {
                return
            }
            node.color = Node.Color.GREY

            val idom = node.doms.filter { it.id != node.id }.minBy { it.id }
            if (idom != null) {
                result.add(Pair(idom, node))
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

fun remarkEdgeIds(root: Node) {
    val indexGenerator = generateSequence(0) { it + 1 }.iterator()
    depthFirstEdgeSearch(root) { edge -> edge.id = indexGenerator.next() }
}
