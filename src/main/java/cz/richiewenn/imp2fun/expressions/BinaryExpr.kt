package cz.richiewenn.imp2fun.expressions

class BinaryExpr(
    val left: Expr,
    val right: Expr,
    val operator: Operator
): Expr {
    override fun toString(): String {
        return "$left${operator.value}$right"
    }
}

enum class Operator (
    val value: String
) {
    OR("||"), AND("&&"), BINARY_OR("|"), BINARY_AND("&"), XOR("^"), EQUALS("=="), NOT_EQUALS("!="), LESS("<"), GREATER(">"), LESS_EQUALS("<="), GREATER_EQUALS(">="), LEFT_SHIFT("<<"), SIGNED_RIGHT_SHIFT(">>"), UNSIGNED_RIGHT_SHIFT(">>>"), PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), REMAINDER("%")
}
