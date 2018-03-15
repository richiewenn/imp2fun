package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.LinkSource

data class LetRec(
    val variableName: String,
    var variableAssignment: Ast, // FunctionCallAstLeaf or ConstantAstLeaf
    val inBody: Ast
): AstNode(
    listOf(variableAssignment, inBody)
) {
    override fun print(): String {
        return "${this.javaClass.simpleName} $variableName"
    }

    override fun printCode(): String {
        return """
            let $variableName = ${variableAssignment.printCode()} in
            ${inBody.printCode()}
        """.trimIndent()
    }

}