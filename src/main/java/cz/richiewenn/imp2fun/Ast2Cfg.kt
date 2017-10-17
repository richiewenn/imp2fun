package cz.richiewenn.imp2fun

import com.github.javaparser.ast.Node as AstNode
import cz.richiewenn.imp2fun.cfg.*

object Ast2Cfg {
    fun toCFG(nodes: List<AstNode>): Node {
        return nodes.map { node ->
            return@map when (node.metaModel.typeName) {
                "MethodDeclaration" -> Node()
                "ForStmt" -> forToCFG(node)
                "UnaryExpr", "BinaryExpr", "VariableDeclarationExpr", "AssignExpr",
                "ExpressionStmt" -> Node(Edge(Node(), node.toString()))
                "IfStmt" -> ifToCFG(node)
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
        val helpOtherwiseNode = Node(Edge(exp = "JUMP"))
        val otherwise = Edge(helpOtherwiseNode, exp = "OTHERWISE")
        condition.edges = listOf(otherwise).plus(condition.edges)
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
            Edge(Node(), "OTHERWISE"),
            Edge(body, astCondition.toString())
        )
        body.lastLeft().edges = listOf(Edge(condition, "JUMP"))
        defI.edges.first().nodes = listOf(condition)

        return defI
    }
}