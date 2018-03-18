package cz.richiewenn.imp2fun.expressions

data class ReturnExpr(
    var name: String
) : Expr {
    override fun toString(): String {
        return name
    }
}