package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.ArrayList

class DotConverter {
    private val stack: Stack<Node> = Stack()

    fun convert(node: Node?): List<String> {
        if(node == null) {
            return emptyList()
        }
        this.stack.add(node)
        val result = ArrayList<String>()
        for (edge in node.outEdges) {
            result.addAll(edge.nodes.flatMap {
                if (this.stack.contains(it)) {
                    return@flatMap listOf("${node.id}->${it.id} [label=\"${edge.exp}\"]")
                } else {
                    return@flatMap listOf("${node.id}->${it.id} [label=\"${edge.exp}\"]").plus(this.convert(it))
                }
            })
        }
        return result
    }
}