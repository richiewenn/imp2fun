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
    val depth: Int
        get() {
            var n: Ast? = this
            var i = 0
            while (n?.parent != null) {
                i++
                n = n.parent
            }
            return i
        }

    fun print(): String
    fun printCode(): String
    fun printBeautifulCode(parent: Ast? = null, offset: Int = 0): String = this.printCode()
    fun getDotLinkSources(): Node
    operator fun plus(list: List<Ast>) = listOf(this)+list
}

open class AstNode(
    children: MutableList<Ast>
) : Ast {
    var children = children
        set(value) {
            field = value
            this.children.forEach { it.parent = this }
        }
    override var parent: AstNode? = null
    init {
        this.children.forEach { it.parent = this }
    }
    override fun getDotLinkSources(): Node = node("[$id] ${this.print()}").link(*this.children.map{ it.getDotLinkSources() }.toTypedArray())
    override val id = Ast.lastId++
    override fun toString(): String {
        return "AstNode(children=$children)"
    }
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
${this.children.joinToString(lineSeparator()) { it.printCode() }}
""".trimIndent()
    override fun printBeautifulCode(parent: Ast?, offset: Int) = """
${this.children.joinToString(lineSeparator()) { it.printBeautifulCode(this) }}
"""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AstNode) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}

open class AstLeaf : Ast {
    override var parent: AstNode? = null
    override fun getDotLinkSources(): Node = node("[$id] ${this.print()}")
    override val id = Ast.lastId++
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AstNode) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}


