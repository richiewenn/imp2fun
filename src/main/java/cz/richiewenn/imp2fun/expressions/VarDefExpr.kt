package cz.richiewenn.imp2fun.expressions

class VarDefExpr(
    val name: String

) : Expr {

    override fun toString(): String {
        return name
    }
}