package cz.richiewenn.imp2fun.haskell.ast

data class IfElseExpressionAstNode(
    val condition: Ast,
    val ifBody: Ast,
    val elseBody: Ast
): AstNode(
    listOf(condition, ifBody, elseBody)
) {
    override fun print() = "\"${this.javaClass.simpleName}\""
}