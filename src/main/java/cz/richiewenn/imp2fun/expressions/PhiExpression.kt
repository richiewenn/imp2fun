package cz.richiewenn.imp2fun.expressions

data class PhiExpression(
    val target: String,
    val vars: Array<String>
) : Expr

data class PhiExpressions(
    val phis: List<PhiExpression>
) : Expr