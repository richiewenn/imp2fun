package cz.richiewenn.imp2fun.haskell.ast

data class ConstantAstLeaf(
    val value: String
): AstLeaf() {
    override fun print() = "\"${this.javaClass.simpleName} $value\""
}