package cz.richiewenn.imp2fun.expressions

class ArrUsageExpr(
    val arrayName: String,
    val index: Expr
): Expr {
    override fun toString(): String {
        return "$arrayName[$index]"
    }
}