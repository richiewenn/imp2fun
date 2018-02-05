package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.*
import cz.richiewenn.imp2fun.haskell.ast.*

object HaskellAstConverter {

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
            listOf(IfElseExpressionAstNode(mapExpression(node.outEdges[0].exp), mapNode(node.outEdges[0].node)[0], AstLeaf())) + mapNode(node.outEdges[0].node)
        } else if (node.outEdges.size == 2 && node.outEdges[1].exp is OtherwiseExpr && node.outEdges[0].exp is BinaryExpr) {
            listOf(IfElseExpressionAstNode(mapExpression(node.outEdges[1].exp), mapNode(node.outEdges[1].node)[0], AstLeaf())) + mapNode(node.outEdges[1].node)
        } else {
//            node.outEdges.flatMap { mapEdge(it) } + node.dominanceFrontiers.flatMap { mapNode(it) }
            node.outEdges.flatMap { mapEdge(it) }
        }
    }

    fun mapEdge(edge: Edge): List<Ast> {
        val exp = edge.exp
        return listOf(when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            is VarAssignExpr -> ArgumentlessFunctionAstNode((exp.target as VarDefExpr).name, mapExpression(exp.value))
//            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
//            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
//            is VarUsageExpr -> AstLeaf()
//            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> mapExpression(exp)
        }) + mapNode(edge.node)
    }

    fun mapExpression(exp: Expr): Ast {
        return when (exp) {
            is ConstantExpr -> ConstantAstLeaf(exp.value)
            is VarAssignExpr -> ArgumentlessFunctionAstNode((exp.target as VarDefExpr).name, mapExpression(exp.value))
//                    is VarDefExpr ->
            is BinaryExpr -> EqAstNode(mapExpression(exp.left), mapExpression(exp.right)) // TODO: this is just equals, do the same for compare
            is OtherwiseExpr -> AstLeaf() //ArgumentlessFunctionAstNode()
            is VarUsageExpr -> AstLeaf()
//                    is Operator ->
            is PhiExpression -> FunctionAstNode("phi", exp.vars.map { FunctionCallAstLeaf(it, emptyList()) }, AstLeaf())
            else -> AstLeaf()
        }
    }


}
