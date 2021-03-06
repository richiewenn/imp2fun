package cz.richiewenn.imp2fun.haskell.ast

data class MainNode(
    val body: Ast
) : AstNode(
    mutableListOf(body)
) {
    override fun print() = "mainFunction"
    override fun printCode() = """
mainFunction = ${body.printCode().replace("\n", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ")}
main = putStrLn (show (mainFunction))
    """.trimIndent()

    override fun printBeautifulCode(parent: Ast?, offset: Int): String {
        val mainFunction = "mainFunction = "
        return """
$mainFunction${body.printBeautifulCode(this, mainFunction.length)}
main = putStrLn (show (mainFunction))
    """.trimIndent()
    }

}