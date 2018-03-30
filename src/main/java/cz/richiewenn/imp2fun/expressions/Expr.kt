package cz.richiewenn.imp2fun.expressions

import java.io.Serializable

interface Expr : Serializable {

    fun getVarUsageExprs(): List<VarUsageExpr> {
        fun get(list: List<VarUsageExpr>, expr: Expr): List<VarUsageExpr> {
            return when(expr) {
                is VarAssignExpr -> list + get(list, expr.value)
                is VarUsageExpr -> list + expr
                is BinaryExpr -> get(list, expr.left) + get(list, expr.right)
                is ReturnExpr -> list + expr.returnExpr
                else -> emptyList()
            }
        }
        return get(emptyList(), this)
    }
}