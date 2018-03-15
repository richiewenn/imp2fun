package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.haskell.ast.Ast
import cz.richiewenn.imp2fun.haskell.ast.AstLeaf
import cz.richiewenn.imp2fun.haskell.ast.AstNode
import java.util.*
import kotlin.collections.ArrayList
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.Graph
import guru.nidi.graphviz.model.Label
import guru.nidi.graphviz.model.LinkSource

class DotConverter {
    private val stack: Stack<Node> = Stack()

//    fun getGraph(node: Node, graphName: String = "graph"): Graph {
//        if (node == null) {
//            return graph(graphName)
//        }
//        this.stack.add(node)
//        val result = ArrayList()
//        for (edge in node.outEdges) {
//            if (edge.node == null) {
//                continue
//            }
//            result.addAll(
//                if (this.stack.contains(edge.node)) {
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp}\"]", "${node.id} [label=\"${node.id} ${node.dominanceFrontiers.map { it.id }}\"]")
//                } else {
////                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]") + this.convert(edge.node)
//                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp}\"]", "${node.id} [label=\"${node.id} ${node.dominanceFrontiers.map { it.id }}\"]") + this.convert(edge.node)
////                    listOf("${node.id}->${edge.node!!.id} [label=\"${edge.exp} ${edge.orientation}\"]", "${node.id} [label=\"${node.id} ${node.doms.map { it.id }}\"]") + this.convert(edge.node)
////                    listOf("${node.id}->${edge.node!!.id} [label=\"${node.doms}\"]").plus(this.convert(edge.node))
////                    listOf("${node.id}->${edge.node!!.id}").plus(this.convert(edge.node))
//                }
//            )
//        }
//        return graph(graphName)
//            .directed()
//            .with()
//    }

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

    fun convertToGraph(ast: Ast): Graph {
        fun convertToNodes(a: Ast): Array<guru.nidi.graphviz.model.Node> {
            return when(a) {
                is AstNode -> a.children.flatMap { convertToNodes(it).toList() } + a.children.map {
                    node(a.id.toString()+" "+a.print()).link(node(it.id.toString()+" "+it.print()))
                }
                else -> emptyList()
            }.toTypedArray()
        }
        return graph("graph")
            .directed()
            .with(
                *convertToNodes(ast)
            )
    }

}