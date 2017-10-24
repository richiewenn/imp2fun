package cz.richiewenn.imp2fun.expressions

class VarUsageExpr (
    val variableName: String
): Expr {
    override fun toString(): String {
        return variableName
    }
}