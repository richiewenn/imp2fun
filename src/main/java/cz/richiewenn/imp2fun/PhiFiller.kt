package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.VarAssignExpr
import cz.richiewenn.imp2fun.expressions.VarDefExpr

object PhiFiller {

    fun fill(node: Node): Node {
        val frontiers = DominanceFrontiers.calculate(node)
        deepSearch(node) { searchedNode ->
            if (!frontiers.contains(searchedNode)) {
                return@deepSearch
            }
            val defs: MutableMap<Node, VarDefExpr> = HashMap()
            fun goUpper(n: Node) {
                n.inEdges.forEach {
                    if (it.node != null && it.node?.dominanceFrontiers?.contains(searchedNode)!!) {
                        goUpper(it.node!!)
                    }
                    val expr = it.node?.outEdges?.filter { it.node == n }?.first()?.exp
                    if (expr is VarAssignExpr) {
                        if (expr.target is VarDefExpr) {
                            defs.set(it.node!!, expr.target)
                        }
                    }

                }
            }
            goUpper(searchedNode)
            val args = defs.values.filter { def -> defs.values.filter { it == def }.size >= 2 }.toSet().map { it.name }.toTypedArray()
            val phiNode = Node(searchedNode.outEdges)
            val phis = Edge(phiNode, PhiExpression(args))
            searchedNode.outEdges = listOf(phis)
            phiNode.inEdges = setOf(Edge(searchedNode)).toMutableSet()

        }
        return node
    }
}