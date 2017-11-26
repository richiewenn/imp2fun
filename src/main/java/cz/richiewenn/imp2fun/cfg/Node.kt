package cz.richiewenn.imp2fun.cfg

class Node(var outEdges: List<Edge> = emptyList()) {
    constructor(vararg edges: Edge) : this(edges.toList())
    var inEdges: MutableSet<Edge> = HashSet()

    companion object { var lastId = 0 }
    var id = lastId++
    var color: Node.Color = Node.Color.WHITE
    var doms: Set<Node> = HashSet()
    var dominanceFrontiers = HashSet<Node>()

    fun idom() = this.doms.filter { it.id != this.id }.minBy { it.id } ?: this
    fun children(): List<Node> = this.outEdges.map { it.node }.filter { it != null } as List<Node>
    fun parents(): List<Node> = this.inEdges.map { it.node }.filter { it != null } as List<Node>
    fun fwParents(): List<Node> = this.inEdges.filter { it.orientation == Edge.Orientation.FORWARD }.map { it.node }.filter { it != null } as List<Node>
    fun resetColors() {
        color = Color.WHITE
        this.children().filter { it.color != Color.WHITE }.forEach { it.resetColors() }
    }
    fun plusLeft(node: Node): Node {
        if(this.lastLeft().outEdges.isEmpty()) {
            this.lastLeft().outEdges = node.outEdges
        } else {
            this.lastLeft().outEdges.first().node = node
        }
        return this
    }

    fun lastLeft(): Node {
        var last: Node = this
        while(last.outEdges.isNotEmpty() && last.outEdges.first().node != null) {
            last = last.outEdges.first().node!!
        }
        return last
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    enum class Color {
        WHITE, GREY, BLACK
    }

}