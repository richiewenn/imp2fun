package cz.richiewenn.imp2fun.cfg

import cz.richiewenn.imp2fun.expressions.EmptyExpr
import cz.richiewenn.imp2fun.expressions.Expr
import java.io.Serializable

// TODO: Get rid of this nullable type Node?
class Edge(var node: Node? = null, val exp: Expr = EmptyExpr(), var orientation: Orientation = Orientation.FORWARD) : Serializable {
    companion object { var lastId = 0 }
    var id = lastId++

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        if (node?.id != other.node?.id) return false

        return true
    }

    override fun hashCode(): Int {
        return if(node != null) node!!.id else id
    }

    enum class Orientation {
        FORWARD, BACKWARD
    }
}