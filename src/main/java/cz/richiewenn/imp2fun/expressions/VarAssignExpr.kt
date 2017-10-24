package cz.richiewenn.imp2fun.expressions

class VarAssignExpr (
    val target: Expr,
    val value: Expr

) : Expr {

    override fun toString(): String {
        return "$target=$value"
    }
}