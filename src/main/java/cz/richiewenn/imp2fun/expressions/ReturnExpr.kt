package cz.richiewenn.imp2fun.expressions

data class ReturnExpr(
    var name: String
) : Expr {
    val returnExpr = VarUsageExpr(name)
    override fun toString(): String {
        return returnExpr.variableName
    }
}