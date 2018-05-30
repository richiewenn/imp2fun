package cz.richiewenn.imp2fun.haskell.ast

import cz.richiewenn.imp2fun.expressions.Operator

data class BinaryAstNode(
    val left: Ast,
    val right: Ast,
    val operator: Operator
): AstNode(
    mutableListOf(left, right)
) {
    override fun print() = "${left.print()} ${operator.forHaskell()} ${right.print()}"
    override fun printCode() = "${left.printCode()} ${operator.forHaskell()} ${right.printCode()}"
    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        return "${left.printBeautifulCode(this)} ${operator.forHaskell()} ${right.printBeautifulCode(this)}"
    }
}