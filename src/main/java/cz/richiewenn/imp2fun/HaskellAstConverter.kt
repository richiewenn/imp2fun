package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.*
import cz.richiewenn.imp2fun.filters.phiFunctions
import cz.richiewenn.imp2fun.haskell.ast.*

object HaskellAstConverter {
    fun convertV2(root: Node) {
        depthFirstSearch(root) { node ->
            println(node.outEdges.filter { it.exp is PhiExpression }.map { "function ${it.exp} ${node.doms}" })
        }
    }

    fun convert(root: Node): Ast {
        fun inner(currentRoot: Node?): Ast {
            if (currentRoot == null) {
                return AstLeaf()
            }

            return AstNode(mapNode(currentRoot))
        }
        return inner(root)
    }
    fun mapNode(node: Node?): List<Ast> {
        if (node == null) {
            return listOf(AstLeaf())
        }
        return if (node.outEdges.size == 2 && node.outEdges[0].exp is OtherwiseExpr && node.outEdges[1].exp is BinaryExpr) {
            listOf(IfElseExpressionAstNode(condition = mapExpression(node.outEdges[1].exp), ifBody = mapNode(node.outEdges[1].node)[0], elseBody = AstLeaf())) + mapNode(node.outEdges[0].node)
        } else {
            node.outEdges.flatMap { mapEdge(it) }
        }
    }

//    fun extractIfBodyAssignments(node: Node): List<Ast> {
//        val n = node.outEdges[1].node
//        n?.outEdges[0].exp
//    }

    fun mapEdge(edge: Edge): List<Ast> {
        val exp = edge.exp
        val nodes = mapNode(edge.node)
        return listOf(when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            //(exp.target as VarDefExpr).name, mapExpression(exp.value)
            is VarAssignExpr -> if(nodes.isNotEmpty()) {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value), nodes.first())
            } else {
                LetRec((exp.target as VarDefExpr).name, mapExpression(exp.value), AstLeaf())
            }
//            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
//            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
//            is VarUsageExpr -> AstLeaf()
//            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> mapExpression(exp)
        })
    }

    fun mapExpression(exp: Expr): Ast {
        return when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            is VarAssignExpr -> ArgumentlessFunctionAstNode((exp.target as VarDefExpr).name, mapExpression(exp.value))
//            is VarDefExpr ->
            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
            is VarUsageExpr -> FunctionCallAstLeaf(exp.variableName, emptyList())
//                    is Operator ->
            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> AstLeaf()
        }
    }


}
