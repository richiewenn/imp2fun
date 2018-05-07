package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.Factory
import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.model.Label
import guru.nidi.graphviz.model.Node

data class IfElseExpressionAstNode(
    val condition: Ast,
    val ifBody: Ast,
    val elseBody: Ast
): AstNode(
    mutableListOf(condition, ifBody, elseBody)
) {
    override fun print() = "If Then Else"
    override fun printCode() = """
if ${condition.printCode()}
  then ${ifBody.printCode()}
  else ${elseBody.printCode()}
""".trimIndent()

    override fun getDotLinkSources(): Node = Factory.node("[$id] ${this.print()}")
        .link(Factory.to(this.condition.getDotLinkSources()).with(Label.of("if")))
        .link(Factory.to(this.ifBody.getDotLinkSources()).with(Label.of("then")))
        .link(Factory.to(this.elseBody.getDotLinkSources()).with(Label.of("else")))
}

data class IfElseAssignmentAstNode(
    val condition: Ast,
    val target: String,
    val ifValue: String,
    val elseValue: String
): AstNode(
    mutableListOf(condition)
) {
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
$target -> if ${condition.printCode()} = $ifValue
else = $elseValue
""".trimIndent()
}