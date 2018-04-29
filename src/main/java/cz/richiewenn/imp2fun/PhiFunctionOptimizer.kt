package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpressions
import cz.richiewenn.imp2fun.expressions.VarDefExpr

class PhiFunctionOptimizer {
    /** Removes phi functions which defines variable which is dead (never used after this definition) */
    fun optimize(root: Node): Node {
        depthFirstEdgeSearch(root) { edge ->
            val expression = edge.exp
            if(expression !is PhiExpressions) {
                return@depthFirstEdgeSearch
            } else {
                expression.phis = expression.phis
                    .filter { phi -> isThereAnUsagesOf(phi.target, edge) }
                    .toMutableList()
            }
        }

        return root
    }

    private fun isThereAnUsagesOf(target: VarDefExpr, edge: Edge): Boolean {
        val isThere = edge.node?.outEdges?.any { it.exp.getVarUsageExprs().any { getOriginalName(it.variableName) == getOriginalName(target.name) } } ?: false
        if(isThere) {
            return true
        } else if(edge.node?.outEdges?.all { it.exp is PhiExpressions } == true) {
            return false
        } else {
            return edge.node?.outEdges?.filterNot { it.exp is PhiExpressions }?.any { isThereAnUsagesOf(target, it) } ?: false
        }
    }
}