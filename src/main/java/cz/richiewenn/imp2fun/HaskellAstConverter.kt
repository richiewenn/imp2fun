package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.*
import cz.richiewenn.imp2fun.haskell.ast.*
import java.util.concurrent.LinkedBlockingQueue

fun getOriginalName(variable: String): String {
    val suffixRegex = Regex("_\\d*\$")
    return variable.replace(suffixRegex, "")
}

object HaskellAstConverter {
    val globalFunctions = ArrayList<FunctionAstNode>()
    val phiCalls = ArrayList<Pair<FunctionCallAstLeaf, Expr>>()

    var iHaveBeenThere = HashSet<Int>()
    fun convert(root: Node): Ast {
        iHaveBeenThere = HashSet()
        fun inner(currentRoot: Node?): Ast {
            if (currentRoot == null) {
                return AstLeaf()
            }

            val nodes = mapNode(currentRoot) // This must be executed early so the globalFunctions are populated
            val gf = this.globalFunctions
                    .groupBy { it.name }
                    .values
                    .map {
                        it.maxBy { it.size }!!
                    }
            // Go up and find what argument names should be used to call this phi functions
            fun getVarName(parent: Ast?, arg: String): String? {
                if (parent == null) {
                    return null
                }
                if (parent is LetRec) {
                    if (getOriginalName(parent.variableName) == getOriginalName(arg)) {
                        return parent.variableName
                    }
                }
                return null
            }
            this.phiCalls.forEach {phi ->
                phi.first.args = phi.first.args.map { arg ->
                    val parent = phi.first.parent
                    val name = getVarName(parent, arg)
                    if(name != null) {
                        return@map name
                    }
                    return@map getVarName(parent?.parent, arg)
                }.filterNotNull()
            }

            return AstNode(gf+MainNode(nodes.first()))
        }
        return inner(root)
    }

    fun mapNode(node: Node?): List<Ast> {
        if (node == null || iHaveBeenThere.contains(node.id)) {
            val first = node?.outEdges?.firstOrNull()
            if(first != null && (first.exp is PhiExpression || first.exp is PhiExpressions)) {
                return mapEdge(first)
            }
            return listOf(AstLeaf())
        }
        iHaveBeenThere.add(node.id)
        return if (node.outEdges.size == 2 && node.outEdges[0].exp is OtherwiseExpr && node.outEdges[1].exp is BinaryExpr) {
            val otherwise = mapNode(node.outEdges[0].node)
            IfElseExpressionAstNode(
                condition = mapExpression(node.outEdges[1].exp),
                ifBody = mapNode(node.outEdges[1].node)[0],
                elseBody = otherwise[0]
            ) + otherwise
        } else {
            node.outEdges.flatMap { mapEdge(it) }
        }
    }


    fun mapEdge(edge: Edge): List<Ast> {
        val exp = edge.exp
        val nodes = mapNode(edge.node)
        return listOf(when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            //(exp.target as VarDefExpr).name, mapExpression(exp.value)
            is ReturnExpr -> FunctionCallAstLeaf(exp.returnExpr.variableName, emptyList())
            is VarAssignExpr -> if(nodes.isNotEmpty()) {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value), nodes.first())
            } else {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value), FunctionCallAstLeaf(exp.target.name))
            }
            is PhiExpression -> {
                val f = FunctionAstNode("phi_${edge.id}", listOf(exp.target), nodes.first())
                this.globalFunctions.add(f)
                val index = this.globalFunctions.count { it == f }-1
                if(index >= exp.vars.size) { // some stuff runs more times then it should so when it already run, we can just skip
                    return emptyList()
                }
                val phiFun = FunctionCallAstLeaf("phi_${edge.id}", args = exp.vars[index])
                this.phiCalls.add(Pair(phiFun, exp))
                phiFun
//                FunctionCallAstLeaf("phi_${edge.id}", args = findLatestDefinition(exp.target, edge.node))
            }
            is PhiExpressions -> {
                val f = FunctionAstNode("phi_${edge.id}", exp.phis.map { it.target }, nodes.first())
                this.globalFunctions.add(f)
                val index = this.globalFunctions.count { it == f }-1
                if(index >= exp.phis.size) { // some stuff runs more times then it should so when it already run, we can just skip
                    return emptyList()
                }
                val phiFun = FunctionCallAstLeaf("phi_${edge.id}", args = exp.phis.map { it.vars[index] }.toList())
                this.phiCalls.add(Pair(phiFun, exp))
                phiFun
            }

//            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
//            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
//            is VarUsageExpr -> AstLeaf()
//            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> mapExpression(exp)
        })
    }

    /** Go up in the graph and find definitions of [target], take the one with the shortest path
     * @param target e.g. a_2
     * */
    private fun findLatestDefinition(target: String, node: Node?): String {
        val queue = LinkedBlockingQueue<Node>()

        fun search(target: String): String {
            val currentNode = queue.poll() ?: throw TODO() // I mean, this should not happen, I think...
            fun getOriginalNameFromExpr(variable: Expr): String {
                return getOriginalName(when(variable) {
                    is VarDefExpr -> variable.name
                    is VarAssignExpr -> getOriginalNameFromExpr(variable.target)
                    else -> ""
                })
            }
            fun getNameFromExpr(variable: Expr): String {
                return when(variable) {
                    is VarDefExpr -> variable.name
                    is VarAssignExpr -> getNameFromExpr(variable.target)
                    else -> ""
                }
            }

            val definition = currentNode.outEdges.find {
                it.exp is VarAssignExpr && getOriginalNameFromExpr(it.exp) == getOriginalName(target)
            }
            return if(definition == null) { //continue search
                currentNode.inEdges.forEach{ queue.add(it.node) }
                search(target)
            } else {
                getNameFromExpr(definition.exp)
            }
        }
        queue.add(node)
        return search(target)
    }

    fun mapExpression(exp: Expr): Ast {
        return when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            is VarAssignExpr -> ArgumentlessFunctionAstNode((exp.target as VarDefExpr).name, mapExpression(exp.value))
//            is VarDefExpr ->
            is BinaryExpr -> BinaryAstNode(mapExpression(exp.left), mapExpression(exp.right), exp.operator)
            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
            is VarUsageExpr -> FunctionCallAstLeaf(exp.variableName, emptyList())
//                    is Operator ->
            else -> AstLeaf()
        }
    }


}
