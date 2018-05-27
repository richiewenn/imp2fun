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

    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        val offsetSpaces = " ".repeat(offset+3)
        return """
if ${condition.printBeautifulCode(this)}
${offsetSpaces}then ${ifBody.printBeautifulCode(this, offset+8)}
${offsetSpaces}else ${elseBody.printBeautifulCode(this, offset+8)}
""".trimStart().trimEnd()
    }

    override fun getDotLinkSources(): Node = Factory.node("[$id] ${this.print()}")
        .link(Factory.to(this.condition.getDotLinkSources()).with(Label.of("if")))
        .link(Factory.to(this.ifBody.getDotLinkSources()).with(Label.of("then")))
        .link(Factory.to(this.elseBody.getDotLinkSources()).with(Label.of("else")))
}