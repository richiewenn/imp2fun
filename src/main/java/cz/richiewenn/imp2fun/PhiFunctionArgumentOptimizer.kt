package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpressions
import cz.richiewenn.imp2fun.expressions.VarDefExpr

/**
 * Another stage of optimalization.
 * Searches if all the newly created variables (targets) are really used.
 */
class PhiFunctionArgumentOptimizer {
    fun optimize(root: Node): Node {
        depthFirstEdgeSearch(root) { edge ->
            val expression = edge.exp
            if (expression !is PhiExpressions) {
                return@depthFirstEdgeSearch
            } else {
                expression.phis = expression.phis
                    .filterNot { phi -> isThereDefinitionBeforeAnUsagesOf(phi.target, edge) }
                    .toMutableList()
            }
        }

        return root
    }

    private fun isThereDefinitionBeforeAnUsagesOf(target: VarDefExpr, edge: Edge): Boolean {
        val iHaveBeenThere = HashSet<Edge>()
        iHaveBeenThere.add(edge)
        fun f(edges: List<Edge>): Boolean {
            val haveNotBeenThereYet = edges.filter { !iHaveBeenThere.contains(it)}
            iHaveBeenThere.addAll(haveNotBeenThereYet)
            val realUsage = haveNotBeenThereYet.any { it.exp.getVarUsageExprs().any { it.variableName == target.name } }
            if(realUsage) { // Found usage before a definition, so it's OK
                return false
            }
            val definition = haveNotBeenThereYet.any { it.exp.getVarDefExprs().any { it.name == target.name } }
            if(definition) { // Found redefinition of the same variable before its usage, so it's NOT OK
                return true
            }
            return haveNotBeenThereYet
                .mapNotNull { it.node?.outEdges }
                .map { it.filterNot { it.exp is PhiExpressions } }
                .map { f(it) }
                .all { it }
        }
        return f(edge.node?.outEdges ?: emptyList())
    }
}