package cz.richiewenn.imp2fun.expressions

class VarUsageExpr (
    var variableName: String
): Expr {
    override fun toString(): String {
        return variableName
    }
}