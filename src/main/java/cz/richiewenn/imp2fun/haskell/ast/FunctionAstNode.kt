package cz.richiewenn.imp2fun.haskell.ast

import guru.nidi.graphviz.model.Factory
import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.model.Label
import guru.nidi.graphviz.model.Node

data class FunctionAstNode(
    val name: String,
    val args: List<String>,
    var body: Ast
) : AstNode(
    mutableListOf(body)
) {
    var theRest: Ast? = null
        set(value) {
            field = value
            if (value != null)
                this.children = (this.children + value).toMutableList()
        }

    override fun print() = "def fun $name(${args.joinToString(", ")})"
    override fun printCode() = """
let $name ${args.joinToString(" ")} = ${body.printCode()}
in
${theRest?.printCode() ?: ""}
    """.trimIndent()

    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        val argsCode = args.joinToString(" ")
        val offsetBase = offset
        val printIn = (theRest !is LetRec && theRest !is FunctionAstNode)
        return if(parent is LetRec || parent is FunctionAstNode) {
            val offsetSpaces = " ".repeat(if(printIn) offset-1 else offset+3)
            """
$name $argsCode = ${body.printBeautifulCode(this, offsetBase)}
$offsetSpaces${if(printIn) "in" else ""} ${theRest?.printBeautifulCode(this, offsetBase) ?: ""}
"""
        } else {
            val offsetSpaces = " ".repeat(if(printIn) offset-4 else offset)
            """
let $name $argsCode = ${body.printBeautifulCode(this, offsetBase+3)}
$offsetSpaces${if(printIn) "in" else ""}${theRest?.printBeautifulCode(this, offsetBase+3) ?: ""}
"""
        }.trimStart().trimEnd()
    }

    override fun getDotLinkSources(): Node = node("[$id] ${this.print()}")
        .link(Factory.to(this.body.getDotLinkSources()).with(Label.of("body")))
        .link(Factory.to(this.theRest?.getDotLinkSources()).with(Label.of("theRest")))

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
    override fun print() = "call $name()"
    override fun printCode() = "$name = ${body.printCode()}"
}

data class FunctionCallAstLeaf(
    val name: String,
    var args: List<String>
) : AstLeaf() {
    constructor(name: String, args: String) : this(name, listOf(args))
    constructor(name: String) : this(name, emptyList())

    override fun print() = if (args.isNotEmpty()) "call $name(${args.joinToString(", ")})" else "call $name"
    override fun printCode() = if (args.isEmpty()) {
        """$name"""
    } else {
        """($name ${args.joinToString(" ") { it }})"""
    }

    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        return if (args.isEmpty()) {
            """$name"""
        } else {
            """($name ${args.joinToString(" ") { it }})"""
        }
    }
}