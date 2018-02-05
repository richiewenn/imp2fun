package cz.richiewenn.imp2fun.haskell.ast

data class FunctionAstNode(
    val name: String,
    val args: List<Ast>,
    val body: Ast
): AstNode(
    listOf(body) + args
) {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
}

data class ArgumentlessFunctionAstNode(
    val name: String,
    val body: Ast
): AstNode(
    listOf(body)
) {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
}

data class FunctionCallAstLeaf(
    val name: String,
    val args: List<Ast>
): AstLeaf() {
    override fun print() = "\"${this.javaClass.simpleName} $name\""
}