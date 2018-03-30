package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.model.Node
import java.lang.System.lineSeparator

interface Ast {
    companion object {
         var lastId = 0
    }
    var parent: AstNode?
    val id: Int
    fun print(): String
    fun printCode(): String
    fun getDotLinkSources(): Node
    operator fun plus(list: List<Ast>) = listOf(this)+list
}

open class AstNode(
    val children: List<Ast>
) : Ast {
    override var parent: AstNode? = null
    init {
        this.children.forEach { it.parent = this }
    }
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
    override var parent: AstNode? = null
    override fun getDotLinkSources(): Node = node("${this.id} ${this.print()}")
    override val id = Ast.lastId++
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = ""
}


