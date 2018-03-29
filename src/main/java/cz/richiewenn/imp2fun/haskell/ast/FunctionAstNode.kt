package cz.richiewenn.imp2fun.haskell.ast

import java.lang.System.lineSeparator

data class FunctionAstNode(
    val name: String,
    val args: List<String>,
    val body: Ast
) : AstNode(
    listOf(body)
) {
    override fun print() = "fun $name(${args.joinToString(", ")})"
    override fun printCode() = """
$name ${args.joinToString(lineSeparator())} = ${body.printCode()}
    """.trimIndent()
    override fun equals(other: Any?): Boolean {
        return other != null && other is FunctionAstNode && other.name == this.name
    }
    override fun hashCode(): Int {
        return this.name.hashCode()
    }
}

data class ArgumentlessFunctionAstNode(
    val name: String,
    val body: Ast
) : AstNode(
    listOf(body)
) {
    override fun print() = "fun $name()"
    override fun printCode() = "$name = ${body.printCode()}"
}

data class FunctionCallAstLeaf(
    val name: String,
    val args: List<String>
) : AstLeaf() {
    constructor(name: String, args: String) : this(name, listOf(args))
    override fun print() = if(args.isNotEmpty()) "$name(${args.joinToString(", ")})" else name
    override fun printCode() = """
($name ${args.map { it }.joinToString(" ")})
    """.trimIndent()
}