package cz.richiewenn.imp2fun.haskell.ast

data class EqAstNode(
    val left: Ast,
    val right: Ast
): AstNode(
    listOf(left, right)
) {
    override fun print() = "\"${toString()}\""
    override fun printCode() = "${left.printCode()} == ${right.printCode()}"
}