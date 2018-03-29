package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.PhiExpressions
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
                    val containsDF = it.node?.dominanceFrontiers?.contains(searchedNode)
                    if (it.node != null && containsDF != null && containsDF) {
                        goUpper(it.node!!)
                    }
                    val expr = it.node?.outEdges?.firstOrNull { it.node == n }?.exp
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
            val originalVariableNames = args.values.map { it.name }.distinct()
            val phiNode = Node(searchedNode.outEdges)
            val argNames = args.values.groupBy { it.name }.values.map { lst -> lst.mapIndexed { i, arg -> arg.name + "_" + i }.toTypedArray()}
            val phiExpr = PhiExpressions(args.values.groupBy { it.name }.values.map {
                lst -> PhiExpression( lst.first().name + "_" + lst.size, lst.mapIndexed { index, arg -> arg.name + "_" + index}.toTypedArray())
            })
            val phis = Edge(phiNode, phiExpr)
            args.entries.groupBy { it.value.name }.values.forEach { lst -> lst.forEachIndexed { i, entry ->
                val originalName = entry.value.name
                val phi = phiExpr.phis.find { getOriginalName(it.target) == originalName }!!
                val newName = phi.vars[i]
                entry.key.outEdges
                    .mapNotNull { it.node }
                    .forEach { goDownAndRemarkUsagesUntilNextAssignment(it, originalName, newName) }
                entry.value.name = newName
            }}

            val iHaveBeenThere = HashSet<Int>()
            fun goDown(n: Node?) {
                if (n == null || iHaveBeenThere.contains(n.id)) {
                    return
                }
                iHaveBeenThere.add(n.id)
                n.outEdges.forEach {
                    val varUsageExprs = it.exp.getVarUsageExprs()
                    varUsageExprs.forEach {
                        if (originalVariableNames.contains(getOriginalName(it.variableName))) {
                            it.variableName = phiExpr.phis.find { phi -> getOriginalName(phi.target) == getOriginalName(it.variableName) }?.target!!
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