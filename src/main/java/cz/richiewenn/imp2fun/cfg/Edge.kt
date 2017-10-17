package cz.richiewenn.imp2fun.cfg

class Edge(var nodes: List<Node> = emptyList(), val exp: String = "") {
    constructor(node: Node, exp: String = "") : this(listOf(node), exp)
    constructor(vararg nodes: Node, exp: String = "") : this(nodes.toList(), exp)
}