package cz.richiewenn.imp2fun.haskell.ast

import java.lang.System.lineSeparator

data class FunctionAstNode(
    val name: String,
    val args: List<String>,
    val body: Ast
) : AstNode(
    mutableListOf(body)
) {
    var theRest: Ast? = null
        set(value) {
            field = value
            if(value != null)
                this.children = (this.children + value).toMutableList()
        }

    override fun print() = "fun $name(${args.joinToString(", ")})"
    override fun printCode() = """
let $name ${args.joinToString(" ")} = ${body.printCode()}
in
${theRest?.printCode() ?: ""}
    """.trimIndent()

    override fun equals(other: Any?): Boolean {
        return other != null && other is FunctionAstNode && other.name == this.name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    val size: Int
        get() {
            return this.printCode().length
        }
}

data class ArgumentlessFunctionAstNode(
    val name: String,
    val body: Ast
) : AstNode(
    mutableListOf(body)
) {
    override fun print() = "fun $name()"
    override fun printCode() = "$name = ${body.printCode()}"
}

data class FunctionCallAstLeaf(
    val name: String,
    var args: List<String>
) : AstLeaf() {
    constructor(name: String, args: String) : this(name, listOf(args))
    constructor(name: String) : this(name, emptyList())

    override fun print() = if (args.isNotEmpty()) "$name(${args.joinToString(", ")})" else name
    override fun printCode() = """
($name ${args.map { it }.joinToString(" ")})
    """.trimIndent()
}