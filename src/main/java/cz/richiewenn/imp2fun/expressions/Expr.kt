package cz.richiewenn.imp2fun.expressions

import java.io.Serializable

interface Expr : Serializable {

    fun getVarUsageExprs(): List<VarUsageExpr> {
        fun get(list: List<VarUsageExpr>, expr: Expr): List<VarUsageExpr> {
            if(expr is VarAssignExpr) {
                return list + get(list, expr.value)
            }
            if(expr is VarUsageExpr) {
                return list + expr
            }
            if(expr is BinaryExpr) {
                return get(list, expr.left) + get(list, expr.right)
            }
            return emptyList()
        }
        return get(emptyList(), this)
    }
}