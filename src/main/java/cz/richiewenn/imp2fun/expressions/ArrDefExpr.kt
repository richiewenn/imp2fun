package cz.richiewenn.imp2fun.expressions

class ArrDefExpr(
    val name: String,
    val index: Expr
) : Expr {

    override fun toString(): String {
        return "$name[$index]"
    }
}