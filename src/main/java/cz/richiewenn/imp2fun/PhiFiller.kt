package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.VarAssignExpr
import cz.richiewenn.imp2fun.expressions.VarDefExpr

object PhiFiller {

    fun goDownAndRemarkUsagesUntilNextAssignment(node: Node, originalVariableName: String, newVariableName: String) {

        fun goDown(currentNode: Node?) {
            if (currentNode == null) {
                return
            }
            currentNode.outEdges
                .filter { it.exp !is VarAssignExpr }
                .forEach {
                    val varUsageExprs = it.exp.getVarUsageExprs()
                    varUsageExprs.forEach {
                        if (it.variableName == originalVariableName) {
                            it.variableName = newVariableName
                        }
                    }
                    goDown(it.node)
            }
        }
        goDown(node)
    }

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
                    val expr = it.node?.outEdges?.first { it.node == n }?.exp
                    if (expr is VarAssignExpr) {
                        if (expr.target is VarDefExpr) {
                            defs[it.node!!] = expr.target
                        }
                    }
                }
            }
            goUpper(searchedNode)
            val args = defs.filter { def -> defs.values.filter { it == def.value }.size >= 2 }
            if (args.isEmpty()) {
                return@depthFirstSearch
            }
            val originalVariableName = args.values.first().name
            val phiNode = Node(searchedNode.outEdges)
            val argNames = args.values.mapIndexed { i, it -> it.name + "_" + i }.toTypedArray()
            val phiExpr = PhiExpression(args.values.first().name + "_" + argNames.size, argNames)
            val phis = Edge(phiNode, phiExpr)
            args.entries.forEachIndexed { i, entry ->
                val originalName = entry.value.name
                val newName = originalName + "_" + i
                entry.key.outEdges
                    .mapNotNull { it.node }
                    .forEach { goDownAndRemarkUsagesUntilNextAssignment(it, originalName, newName) }
                entry.value.name = newName
            }

            fun goDown(n: Node?) {
                if (n == null) {
                    return
                }
                n.outEdges.forEach {
                    val varUsageExprs = it.exp.getVarUsageExprs()
                    varUsageExprs.forEach {
                        if (it.variableName == originalVariableName) {
                            it.variableName = phiExpr.target
                        }
                    }

                    goDown(it.node)
                }
            }
            goDown(searchedNode)


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