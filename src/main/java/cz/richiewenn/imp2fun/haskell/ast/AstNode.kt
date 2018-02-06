package cz.richiewenn.imp2fun.haskell.ast

import java.lang.System.lineSeparator

interface Ast {
    companion object {
         var lastId = 0
    }
    val id: Int
    fun print(): String
    fun printCode(): String
}

open class AstNode(
    val children: List<Ast>
) : Ast {
    override val id = Ast.lastId++
    override fun toString(): String {
        return "AstNode(children=$children)"
    }
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = """
        ${this.children.joinToString(lineSeparator()) { it.printCode() }}
""".trimIndent()
}

open class AstLeaf(
) : Ast {
    override val id = Ast.lastId++
    override fun print() = "\"${this.javaClass.simpleName}\""
    override fun printCode() = ""
}


