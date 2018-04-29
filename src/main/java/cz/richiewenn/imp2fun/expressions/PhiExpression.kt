package cz.richiewenn.imp2fun.expressions

data class PhiExpression(
    val target: VarDefExpr,
    val vars: MutableList<String>,
    val originalName: String = target.name
) : Expr {
    override fun toString(): String {
        return "$target"
    }
}

data class PhiExpressions(
    var phis: MutableList<PhiExpression>
) : Expr