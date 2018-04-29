package cz.richiewenn.imp2fun.expressions

data class PhiExpression(
    val target: VarDefExpr,
    val vars: MutableList<String>,
    val originalName: String = target.name
) : Expr

data class PhiExpressions(
    val phis: MutableList<PhiExpression>
) : Expr