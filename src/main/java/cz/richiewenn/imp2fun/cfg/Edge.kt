package cz.richiewenn.imp2fun.cfg

open class Edge(
    var to: Node? = null,
    var expression: String
) : Dot {
    override fun toDot(label: Boolean): String = if(label) {
        "->${to?.id} [label=\"$expression\"]${System.lineSeparator()}${to?.toDot(label)}"
    } else {
        "->${to?.id}${System.lineSeparator()}${to?.toDot(label)}"
    }

    override fun toString(): String {
        return "-("+expression+")->["+to?.id+"]" + System.lineSeparator() + to?.toString()
    }
}

class JumpEdge(val nodeId: Int) : Edge(null, "JUMP") {

    override fun toDot(label: Boolean): String = if(label) {
        "->$nodeId [label=\"$expression\"]"
    } else {
        "->$nodeId"
    }
    override fun toString(): String {
        return "-($expression)->[$nodeId]"
    }
}

class EndEdge(expression: String = "END") : Edge(null, expression) {
    override fun toDot(label: Boolean): String = "->end"

    override fun toString(): String {
        return "-($expression)"
    }
}