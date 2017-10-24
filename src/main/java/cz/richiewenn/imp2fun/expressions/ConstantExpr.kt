package cz.richiewenn.imp2fun.expressions

class ConstantExpr(
    val value: String
) : Expr {

    override fun toString(): String {
        return value
    }
}