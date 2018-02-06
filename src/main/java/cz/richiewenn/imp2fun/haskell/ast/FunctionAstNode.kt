package cz.richiewenn.imp2fun.haskell.ast

import java.lang.System.lineSeparator

data class FunctionAstNode(
    val name: String,
    val args: List<Ast>,
    val body: Ast
) : AstNode(
    listOf(body) + args
) {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
    override fun printCode() = """
        $name ${args.joinToString(lineSeparator()) { "-> ${it.printCode()}" }}
        $name = ${body.printCode()}
    """.trimIndent()
}

data class ArgumentlessFunctionAstNode(
    val name: String,
    val body: Ast
) : AstNode(
    listOf(body)
) {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
    override fun printCode() = """
        $name = ${body.printCode()}
    """.trimIndent()
}

data class FunctionCallAstLeaf(
    val name: String,
    val args: List<Ast>
) : AstLeaf() {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
    override fun printCode() = """
        ($name ${args.map { it.printCode() }.joinToString(" ")})
    """.trimIndent()
}