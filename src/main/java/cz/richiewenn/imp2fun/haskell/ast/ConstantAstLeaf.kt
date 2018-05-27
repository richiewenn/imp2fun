package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.model.Node

data class ConstantAstLeaf(
    val value: String
) : AstLeaf() {
    override fun print() = " $value"
    override fun printCode() = " $value"
    override fun printBeautifulCode(parent: Ast?, offset: Int) = " $value"
    override fun getDotLinkSources(): Node {
        return node("[${this.id}] $value")
    }
}