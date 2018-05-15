package cz.richiewenn.imp2fun

import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.stmt.ExpressionStmt
import cz.richiewenn.imp2fun.expressions.*
import com.github.javaparser.ast.Node as AstNode

object ExpressionMapper {
    fun map(source: Expression) : Expr {
        return when(source) {
            is BinaryExpr -> cz.richiewenn.imp2fun.expressions.BinaryExpr(this.map(source.left), this.map(source.right), Operator.valueOf(source.operator.name))
            is VariableDeclarationExpr -> VarAssignExpr(this.mapDef(source.variables[0].name), this.map(source.variables[0].initializer.orElse(IntegerLiteralExpr("TODO")))) // TODO: all variables
            is AssignExpr -> VarAssignExpr(this.mapDef(source.target), this.map(source.value))
            is LiteralStringValueExpr -> ConstantExpr(source.value)
            is NameExpr -> VarUsageExpr(source.name.identifier)
            is ArrayCreationExpr -> ArrCreationExpr()
            is ArrayAccessExpr -> ArrUsageExpr(this.map(source.name).toString(), this.map(source.index))
            is UnaryExpr -> when(source.operator) {
                UnaryExpr.Operator.POSTFIX_INCREMENT -> VarAssignExpr(
                    target = VarDefExpr(source.expression.asNameExpr().nameAsString),
                    value = cz.richiewenn.imp2fun.expressions.BinaryExpr(
                        left = cz.richiewenn.imp2fun.expressions.VarUsageExpr(source.expression.asNameExpr().nameAsString),
                        right = cz.richiewenn.imp2fun.expressions.ConstantExpr("1"),
                        operator = Operator.PLUS
                    )
                )
                UnaryExpr.Operator.POSTFIX_DECREMENT -> VarAssignExpr(
                    target = VarDefExpr(source.expression.asNameExpr().nameAsString),
                    value = cz.richiewenn.imp2fun.expressions.BinaryExpr(
                        left = cz.richiewenn.imp2fun.expressions.VarUsageExpr(source.expression.asNameExpr().nameAsString),
                        right = cz.richiewenn.imp2fun.expressions.ConstantExpr("1"),
                        operator = Operator.MINUS
                    )
                )
                else -> TODO(source.operator.toString())
            }
            else -> TODO(source.toString())
        }
    }

    fun mapDef(source: Expression): Expr {
        return when (source) {
            is NameExpr -> VarDefExpr(source.name.identifier)
            is ArrayAccessExpr -> ArrDefExpr((source.name as NameExpr).name.identifier,  this.map(source.index))
            else -> TODO(source.toString())
        }
    }

    fun mapDef(source: AstNode): Expr {
        return when (source) {
            is SimpleName -> VarDefExpr(source.identifier)
            else -> TODO(source.toString())
        }
    }

    fun map(node: AstNode) : Expr {
        return when (node) {
            is SimpleName -> VarUsageExpr(node.identifier)
            is ExpressionStmt -> this.map(node.expression)
            is UnaryExpr -> this.map(node.expression)
            is BinaryExpr -> this.map(node)
            is VariableDeclarationExpr -> this.map(node)
            is AssignExpr -> this.map(node)
            else -> TODO(node.toString())
        }
    }
}