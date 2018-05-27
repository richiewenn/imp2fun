package cz.richiewenn.imp2fun.haskell.ast

data class LetRec(
    val variableName: String,
    var variableAssignment: Ast, // FunctionCallAstLeaf or ConstantAstLeaf
    var inBody: Ast
): AstNode(
    mutableListOf(variableAssignment, inBody)
) {
    override fun print(): String {
        return "let $variableName = ${variableAssignment.print()} in"
    }

    override fun printCode(): String {
        return """let $variableName = ${variableAssignment.printCode()} in
  ${inBody.printCode()}"""
    }

    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        val offsetBase = offset
        val printIn = inBody !is LetRec && inBody !is FunctionAstNode
        return if(parent is LetRec || parent is FunctionAstNode) {
            val offsetSpaces = " ".repeat(if(printIn) offset-4 else offset)
            """
$variableName = ${variableAssignment.printBeautifulCode(this, offsetBase)}
$offsetSpaces${if(printIn) "in" else ""} ${inBody.printBeautifulCode(this, offsetBase)}
"""
        } else {
            val offsetSpaces = " ".repeat(if(printIn) offset-1 else offset+3)
            """
let $variableName = ${variableAssignment.printBeautifulCode(this, offsetBase+3)}
$offsetSpaces${if(printIn) "in" else ""} ${inBody.printBeautifulCode(this, offsetBase+3)}
"""
        }.trimStart().trimEnd()
    }
}