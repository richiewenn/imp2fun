package cz.richiewenn.imp2fun.expressions

import java.io.Serializable

interface Expr : Serializable {

    fun getVarUsageExprs(): List<VarUsageExpr> {
        fun get(list: List<VarUsageExpr>, expr: Expr): List<VarUsageExpr> {
            return when(expr) {
                is VarAssignExpr -> list + get(list, expr.value)
                is VarUsageExpr -> list + expr
                is BinaryExpr -> get(list, expr.left) + get(list, expr.right)
                is ReturnExpr -> get(list, expr.expr)
                is PhiExpressions -> list + expr.phis.map { VarUsageExpr(it.target.name) }
                else -> emptyList()
            }
        }
        return get(emptyList(), this)
    }

    fun getVarDefExprs(): List<VarDefExpr> {
        fun get(list: List<VarDefExpr>, expr: Expr): List<VarDefExpr> {
            return when(expr) {
                is VarAssignExpr -> list + get(list, expr.target)
                is VarDefExpr -> list + expr
                is PhiExpression -> list + expr.target
                is PhiExpressions -> list + expr.phis.map { it.target }
                else -> emptyList()
            }
        }
        return get(emptyList(), this)
    }
}