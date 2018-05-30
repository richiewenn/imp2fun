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
    /** Pair<Function, Level> */
    var globalFunctions: MutableList<Pair<FunctionAstNode, FunctionCallAstLeaf>> = ArrayList()
    val phiCalls = ArrayList<Pair<FunctionCallAstLeaf, Expr>>()

    var iHaveBeenThere = HashSet<Int>()
    fun convert(root: Node): Ast {
        iHaveBeenThere = HashSet()
        fun inner(currentRoot: Node?): Ast {
            if (currentRoot == null) {
                return AstLeaf()
            }

            val nodes = mapNode(currentRoot, 0) // This must be executed early so the globalFunctions are populated

            // Go up and find what argument names should be used to call this phi functions
            fun getVarName(parent: Ast?, arg: String, fromWhere: Ast?): String? {
                if (parent == null) {
                    return arg + "_NOTRESOLVABLEYET"
                }
                if (parent is LetRec) {
                    if (getOriginalName(parent.variableName) == getOriginalName(arg)) {
                        return parent.variableName
                    } else {
                        return getVarName(parent.parent, arg, parent)
                    }
                } else if (parent is FunctionAstNode
                    && parent.theRest != fromWhere
                ) {
                    if (parent.args.any { getOriginalName(it) == getOriginalName(arg) }) {
                        return parent.args.find { getOriginalName(it) == getOriginalName(arg) }!!
                    } else {
                        return getVarName(parent.parent, arg, parent)
                    }
                } else {
                    return getVarName(parent.parent, arg, parent)
                }
            }
            this.phiCalls.forEach { phi ->
                phi.first.args = phi.first.args.map { arg ->
                    val parent = phi.first.parent
                    val name = getVarName(parent, arg, phi.first)
                    return@map name
                }.filterNotNull()
            }

            // Remove duplicates
            val uniqueFunctions = globalFunctions
                .groupBy { it.first.name }
                .map {
                    return@map if (it.value.size > 1) {
                        val notLeafs = it.value.filterNot { it.first.body is AstLeaf }
                        if(notLeafs.isEmpty()) {
                            it.value.first()
                        } else {
                            notLeafs.first()
                        }
                    } else {
                        it.value.first()
                    }
                }
                .toMutableList()
            globalFunctions = uniqueFunctions
            fun insertFunctions(node: Ast) {
                val needToInsert = globalFunctions.filter { it.second == node }.map { it.first }.distinct()
                globalFunctions = globalFunctions.filterNot { needToInsert.any { insert -> insert.name == it.first.name } }.toMutableList()
                if (needToInsert.isNotEmpty()) {
                    fun leaf(a: FunctionAstNode): FunctionAstNode = if (a.theRest == null) a else leaf(a.theRest as FunctionAstNode)
                    needToInsert.forEachIndexed { index, n ->
                        if (index == 0) {
                            return@forEachIndexed
                        }
                        leaf(needToInsert.first()).theRest = n
                    }

                    fun goUpAndFindDef(n: AstNode): AstNode {
                        return if (n is LetRec || n is FunctionAstNode) {
                            n
                        } else {
                            goUpAndFindDef(n.parent!!)
                        }
                    }

                    val n = goUpAndFindDef(node.parent!!)
                    if (n is LetRec) {
                        leaf(needToInsert.first()).theRest = n.inBody

                        n.children = mutableListOf(n.variableAssignment, needToInsert.first())
                        n.inBody = needToInsert.first()
                    } else if (n is FunctionAstNode) {
                        leaf(needToInsert.first()).theRest = n.body

                        n.children = mutableListOf(n.body, needToInsert.first())
                        n.body = needToInsert.first()
                    }
                }
                if (node is AstNode) {
                    node.children.forEach { insertFunctions(it) }
                }

            }

            while (globalFunctions.isNotEmpty()) {
                insertFunctions(nodes.first())
            }

            // There may be some _NOTRESOLVABLEYET variables left from first tree walk, let's resolve them now
            this.phiCalls.forEach { phi ->
                phi.first.args = phi.first.args
                    .mapNotNull { arg ->
                        return@mapNotNull if(arg.endsWith("_NOTRESOLVABLEYET")) {
                            val parent = phi.first.parent
                            getVarName(parent, arg.removeSuffix("_NOTRESOLVABLEYET"), phi.first)
                        } else {
                            arg
                        }
                    }
            }

            return MainNode(nodes.first())
        }
        return inner(root)
    }

    fun mapNode(node: Node?, level: Int): List<Ast> {
        if (node == null || iHaveBeenThere.contains(node.id)) {
            val first = node?.outEdges?.firstOrNull()
            if (first != null && (first.exp is PhiExpression || first.exp is PhiExpressions)) {
                return mapEdge(first, level)
            }
            return listOf(AstLeaf())
        }
        iHaveBeenThere.add(node.id)
        return if (node.outEdges.size == 2 && node.outEdges[0].exp is OtherwiseExpr && node.outEdges[1].exp is BinaryExpr) {
            val otherwise = mapNode(node.outEdges[0].node, level + 1)
            IfElseExpressionAstNode(
                condition = mapExpression(node.outEdges[1].exp, level + 1),
                ifBody = mapNode(node.outEdges[1].node, level + 1)[0],
                elseBody = otherwise[0]
            ) + otherwise
        } else {
            node.outEdges.flatMap { mapEdge(it, level) }
        }
    }

    fun mapEdge(edge: Edge, level: Int): List<Ast> {
        val exp = edge.exp
        val nodes = mapNode(edge.node, level + 1)
        return listOf(when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
        //(exp.target as VarDefExpr).name, mapExpression(exp.value)
            is ReturnExpr -> mapExpression(exp.expr, level + 1)
            is VarAssignExpr -> if (nodes.isNotEmpty()) {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value, level + 1), nodes.first())
            } else {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value, level + 1), FunctionCallAstLeaf(exp.target.name))
            }
            is PhiExpressions -> {
                val funName = "fun_${edge.id}"
                val index = this.globalFunctions.count { it.first.name == funName }
                val defArgs = exp.phis.filter { it.vars.size > index }.map { it.target.name }
                val f = FunctionAstNode(funName, defArgs, nodes.first())
                val args = exp.phis.filter { it.vars.size > index }.map { it.vars[index] }.toList()
                val phiFun = FunctionCallAstLeaf("fun_${edge.id}", args = args)
                this.globalFunctions.add(Pair(f, phiFun))
                this.phiCalls.add(Pair(phiFun, exp))
                phiFun
            }

//            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
//            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
//            is VarUsageExpr -> AstLeaf()
//            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> mapExpression(exp, level)
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
                return getOriginalName(when (variable) {
                    is VarDefExpr -> variable.name
                    is VarAssignExpr -> getOriginalNameFromExpr(variable.target)
                    else -> ""
                })
            }

            fun getNameFromExpr(variable: Expr): String {
                return when (variable) {
                    is VarDefExpr -> variable.name
                    is VarAssignExpr -> getNameFromExpr(variable.target)
                    else -> ""
                }
            }

            val definition = currentNode.outEdges.find {
                it.exp is VarAssignExpr && getOriginalNameFromExpr(it.exp) == getOriginalName(target)
            }
            return if (definition == null) { //continue search
                currentNode.inEdges.forEach { queue.add(it.node) }
                search(target)
            } else {
                getNameFromExpr(definition.exp)
            }
        }
        queue.add(node)
        return search(target)
    }

    fun mapExpression(exp: Expr, level: Int): Ast {
        return when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            is VarAssignExpr -> ArgumentlessFunctionAstNode((exp.target as VarDefExpr).name, mapExpression(exp.value, level + 1))
//            is VarDefExpr ->
            is BinaryExpr -> BinaryAstNode(mapExpression(exp.left, level + 1), mapExpression(exp.right, level + 1), exp.operator)
            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
            is VarUsageExpr -> FunctionCallAstLeaf(exp.variableName, emptyList())
//                    is Operator ->
            else -> AstLeaf()
        }
    }


}
