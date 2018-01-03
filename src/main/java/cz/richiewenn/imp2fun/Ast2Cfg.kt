package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.*
import cz.richiewenn.imp2fun.expressions.JumpExpr
import cz.richiewenn.imp2fun.expressions.OtherwiseExpr
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
                // TODO: ElseStmt
                "BlockStmt" -> toCFG(node.childNodes)
                "ReturnStmt" -> Node()
                else -> Node()
            }
        }.reduce { left, right -> left.plusLeft(right)}
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
        val defI = toCFG(children[0])
        val astCondition = children[1]
        val ipp = toCFG(children[2])
        val body = toCFG(children[3]).plusLeft(ipp)
        val condition = Node(
            Edge(Node(), OtherwiseExpr()),
            Edge(body, ExpressionMapper.map(astCondition))
        )
        body.lastLeft().outEdges = listOf(Edge(condition, JumpExpr(), Edge.Orientation.BACKWARD))
        defI.outEdges.first().node = condition

        return defI
    }
}