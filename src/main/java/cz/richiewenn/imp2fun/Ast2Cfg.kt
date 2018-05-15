package cz.richiewenn.imp2fun

import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.EmptyStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import cz.richiewenn.imp2fun.cfg.*
import cz.richiewenn.imp2fun.expressions.Expr
import cz.richiewenn.imp2fun.expressions.JumpExpr
import cz.richiewenn.imp2fun.expressions.OtherwiseExpr
import cz.richiewenn.imp2fun.expressions.ReturnExpr
import com.github.javaparser.ast.Node as AstNode

object Ast2Cfg {
    fun toCFG(nodes: List<AstNode>): Node {
        return nodes.map { node ->
            return@map when (node.metaModel.typeName) {
                // TODO: when (node) { is ExpressionStmt -> ..... etc.
                "MethodDeclaration" -> Node()
                "ForStmt" -> forToCFG(node)
                "UnaryExpr", "BinaryExpr", "VariableDeclarationExpr", "AssignExpr",
                "ExpressionStmt" -> Node(Edge(Node(), ExpressionMapper.map(node)))
                "IfStmt" -> ifToCFG(node)
                "ElseStmt" -> elseToCFG(node)
                "BlockStmt" -> toCFG(node.childNodes)
                "ReturnStmt" -> Node(Edge(Node(), ReturnExpr(((node as ReturnStmt).expression.orElseThrow {RuntimeException()} as NameExpr).nameAsString )))
                else -> TODO(node.metaModel.typeName)
            }
        }.reduce { left, right -> left.plusLeft(right)}
    }

    private fun elseToCFG(node: com.github.javaparser.ast.Node): Node {
        TODO("not implemented")
    }

    fun toCFG(node: AstNode) = toCFG(listOf(node))

    private fun ifToCFG(node: AstNode): Node {
        val children = node.childNodes
        val condition = toCFG(children[0])
        val body = toCFG(children[1])
        condition.plusLeft(body)
        val helpOtherwiseNode = Node(Edge(exp = JumpExpr()))
        val otherwise = Edge(helpOtherwiseNode, exp = OtherwiseExpr())
        condition.outEdges = listOf(otherwise).plus(condition.outEdges)
        body.plusLeft(helpOtherwiseNode)
        return condition
    }

    private fun forToCFG(node: AstNode): Node {
        val children = node.childNodes
//        val defI = toCFG(children[0])
//        val astCondition = ExpressionMapper.map(children[1])
//        val ipp = toCFG(children[2])
//        val body = toCFG(children[3]).plusLeft(ipp)
//        val body = toCFG(children[2])

//        val condition = Node(
//            Edge(Node(), OtherwiseExpr()),
//            Edge(body, astCondition)
//        )
//        body.lastLeft().outEdges = listOf(Edge(condition, JumpExpr(), Edge.Orientation.BACKWARD))
//        defI.outEdges.first().node = condition
//
//        return defI
        return For(children).getRoot()
    }
}


class For(def: List<AstNode>) {
    val defI = def.find { it is VariableDeclarationExpr }?.toCfg()
    val condition = def.find { it is BinaryExpr }?.toCfg()?.outEdges?.first()
    val ipp: Expression? = def.find { it is AssignExpr || it is UnaryExpr } as? Expression?
    val blockBody = def.find { it is BlockStmt }?.toCfg()
    val isEmptyBody = def.find { it is EmptyStmt } != null

    fun getRoot(): Node {
        val astCondition = Node()
        val backExpr = if(ipp != null) ExpressionMapper.map(ipp) else JumpExpr()
        val backEdge = Edge(astCondition, backExpr, Edge.Orientation.BACKWARD)
        val backNode = Node(backEdge)
        if (this.condition != null) {
            astCondition.outEdges = listOf(
                Edge(Node(), OtherwiseExpr()),
                if (!isEmptyBody) {
                    if(ipp != null) {
                        blockBody?.plusLeft(backNode)
                    }
                    blockBody?.lastLeft()?.outEdges = listOf(Edge(astCondition, JumpExpr(), Edge.Orientation.BACKWARD))
                    condition.node = blockBody
                    condition
                }
                else {
                    condition.node = backNode
                    condition
                }
            )
        } else {

        }

        if(defI != null) {
            defI.outEdges.first().node = astCondition
            return defI
        }
        return astCondition
    }

//    val defI: Node, val condition: Expr, val ipp: Node, val body: Node
//    companion object {
//        fun new(def: List<AstNode>): For {
//            val defI = def.find { it is VariableDeclarationExpr }
//            val condition = def.find { it is AssignExpr }
//            val ipp = def.find { it is BinaryExpr }
//            val blockBody = def.find { it is BlockStmt }
//            val emptyBody = def.find { it is EmptyStmt }
//        }
//    }
}

private fun AstNode.toCfg(): Node {
    return Ast2Cfg.toCFG(this)
}
