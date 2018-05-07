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

}