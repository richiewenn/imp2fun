package cz.richiewenn.imp2fun.haskell.ast

data class MainNode(
    val body: Ast
) : AstNode(
    mutableListOf(body)
) {
    override fun print() = "mainFunction"
    override fun printCode() = """
mainFunction = ${body.printCode()}
main = putStrLn (show (mainFunction))
    """.trimIndent()
}