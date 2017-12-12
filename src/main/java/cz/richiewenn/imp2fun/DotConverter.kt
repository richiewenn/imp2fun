package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.ArrayList

class DotConverter {
    private val stack: Stack<Node> = Stack()

    fun convert(node: Node?): List<String> {
        if (node == null) {
            return emptyList()
        }
        this.stack.add(node)
        val result = ArrayList<String>()
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }
            result.addAll(
                if (this.stack.contains(edge.node)) {
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]")
                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp}\"]", "${node.id} [label=\"${node.id} ${node.dominanceFrontiers.map { it.id }}\"]")
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]", "${node.id} [label=\"${node.id} ${node.doms.map { it.id }}\"]")
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${node.doms}\"]")
//                    listOf("${node.id}->${edge.node!!.id}")
                } else {
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]") + this.convert(edge.node)
                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp}\"]", "${node.id} [label=\"${node.id} ${node.dominanceFrontiers.map { it.id }}\"]") + this.convert(edge.node)
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]", "${node.id} [label=\"${node.id} ${node.doms.map { it.id }}\"]") + this.convert(edge.node)
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${node.doms}\"]").plus(this.convert(edge.node))
//                    listOf("${node.id}->${edge.node!!.id}").plus(this.convert(edge.node))
                }
            )
        }
        return result
    }

}