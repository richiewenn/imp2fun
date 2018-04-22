package cz.richiewenn.imp2fun.expressions

data class PhiExpression(
    val target: String,
    val vars: Array<String>,
    val originalName: String = target
) : Expr

data class PhiExpressions(
    val phis: List<PhiExpression>
) : Expr