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
            if (expression !is PhiExpressions) {
                return@depthFirstEdgeSearch
            } else {
                expression.phis = expression.phis
                    .filter { phi -> isThereAnUsagesOf(phi.target, edge, edge, root) }
                    .toMutableList()
            }
        }

        return root
    }

    private fun isThereAnUsagesOf(target: VarDefExpr, edge: Edge, originalEdge: Edge, root: Node): Boolean {
        val usages = countUsagesOf(target, edge, originalEdge)
        return usages.RHS > 0
//
// val depth = depthFromRoot(root, originalEdge)
//        val isThere = edge.node?.outEdges
////            ?.filter { depthFromRoot(root, it) > depth }
//            ?.any {
//                it.exp.getVarUsageExprs()
//                    .any { getOriginalName(it.variableName) == getOriginalName(target.name) }
//            }
//            ?: false
//        if (isThere) {
//            return true
//        } else if (edge.node?.outEdges?.all { it.exp is PhiExpressions } == true) {
//            return false
//        } else {
//            return edge.node?.outEdges?.filterNot { it.exp is PhiExpressions }?.any { isThereAnUsagesOf(target, it, originalEdge, root) }
//                ?: false
//        }
    }

    fun depthFromRoot(root: Node, edge: Edge): Int {
        fun f(e: List<Edge>, depth: Int): Int {
            return if (e.any { it == edge }) {
                depth
            } else {
                val children = e.mapNotNull { it.node?.outEdges }.flatMap { it }
                f(children, depth + 1)
            }
        }
        return f(root.outEdges, 0)
    }

    private fun countUsagesOf(target: VarDefExpr, e: Edge, originalEdge: Edge): Usages {
        val iHaveBeenThere = HashSet<Edge>()
        fun f(edge: Edge): Usages {
            if (iHaveBeenThere.contains(edge)) {
                return Usages()
            }
            val isThereReal = edge.node?.outEdges
                ?.filterNot { it.exp is PhiExpressions }
                ?.any { it.exp.getVarUsageExprs().any { getOriginalName(it.variableName) == getOriginalName(target.name) } }
                ?: false
            val isThereFun = edge.node?.outEdges
                ?.filter { it.exp is PhiExpressions }
                ?.any { it.exp.getVarUsageExprs().any { getOriginalName(it.variableName) == getOriginalName(target.name) } }
                ?: false
            iHaveBeenThere.add(edge)

            fun goDeeper(): Usages {
                val children = edge.node?.outEdges
                    ?.map { f(it) }
                return if(children != null && children.isNotEmpty()) {
                    children.reduce { acc, usgs -> acc + usgs }
                } else {
                    Usages()
                }
            }

            return when {
                isThereReal -> Usages(RHS = 1) // We found some real usage, so return it.
                isThereFun -> Usages(phi = 1) + goDeeper()
                else -> goDeeper()
            }
        }
        return f(e)
    }

    data class Usages(
        val RHS: Int = 0,
        val phi: Int = 0
    ) {
        operator fun plus(usages: Usages) = this.copy(
            RHS = RHS + usages.RHS,
            phi = phi + usages.phi
        )

    }
}