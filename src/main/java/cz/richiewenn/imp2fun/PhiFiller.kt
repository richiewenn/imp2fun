package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.VarAssignExpr
import cz.richiewenn.imp2fun.expressions.VarDefExpr

object PhiFiller {

    fun fill(node: Node): Node {
        val frontiers = DominanceFrontiers.calculate(node)
        depthFirstSearch(node) { searchedNode ->
            if (!frontiers.contains(searchedNode)) {
                return@depthFirstSearch
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
            val args = defs.filter { def -> defs.values.filter { it == def.value }.size >= 2 }
            val phiNode = Node(searchedNode.outEdges)
            val phis = Edge(phiNode, PhiExpression(args.values.mapIndexed { i, it -> it.name + "_" + i }.toTypedArray()))
            args.values.forEachIndexed { i, it -> it.name = it.name + "_" + i }

            // go from top to down. renaming until you reach new definition
//            fun goUpperRenaming(n: Node, exp: VarDefExpr) {
//                    n.inEdges.forEach {
//                        it.node?.outEdges
//                            ?.filter { it.node == n }
//                            ?.first()
//                            ?.exp
//                            ?.getVarUsageExprs()
//                            ?.forEach {
//                                it.variableName = exp.name
//                            }
//                        val nn = it.node
//                        if (nn != null) {
//                            goUpperRenaming(nn, exp)
//                        }
//                    }
//            }
//            args.forEach { node, exp ->
//                node.outEdges.forEach { it.exp.getVarUsageExprs().forEach { it.variableName = exp.name } }
//            }
            searchedNode.outEdges = listOf(phis)
            phiNode.inEdges = setOf(Edge(searchedNode)).toMutableSet()

        }
        return node
    }
}