package cz.richiewenn.imp2fun.haskell.ast

data class IfElseExpressionAstNode(
    val condition: Ast,
    val ifBody: Ast,
    val elseBody: Ast
): AstNode(
    listOf(condition, ifBody, elseBody)
) {
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
if ${condition.printCode()}
  then ${ifBody.printCode()}
  else ${elseBody.printCode()}
""".trimIndent()
}

data class IfElseAssignmentAstNode(
    val condition: Ast,
    val target: String,
    val ifValue: String,
    val elseValue: String
): AstNode(
    listOf(condition)
) {
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
$target -> if ${condition.printCode()} = $ifValue
else = $elseValue
""".trimIndent()
}