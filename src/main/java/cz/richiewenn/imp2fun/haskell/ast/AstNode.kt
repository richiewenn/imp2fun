package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.Graph
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.LinkSource
import guru.nidi.graphviz.model.Node
import java.lang.System.lineSeparator

interface Ast {
    companion object {
         var lastId = 0
    }
    val id: Int
    fun print(): String
    fun printCode(): String
    fun getDotLinkSources(): Node
    operator fun plus(list: List<Ast>) = listOf(this)+list
}

open class AstNode(
    val children: List<Ast>
) : Ast {
    override fun getDotLinkSources(): Node = node("${this.id} ${this.print()}").link(*this.children.map{ it.getDotLinkSources() }.toTypedArray())
    override val id = Ast.lastId++
    override fun toString(): String {
        return "AstNode(children=$children)"
    }
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
${this.children.joinToString(lineSeparator()) { it.printCode() }}
""".trimIndent()
}

open class AstLeaf : Ast {
    override fun getDotLinkSources(): Node = node("${this.id} ${this.print()}")
    override val id = Ast.lastId++
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = ""
}


