package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Node
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class DominanceTree {
    var stack: Stack<Node> = Stack()
    var idoms = HashSet<Pair<Int, Int>>()
    var doms = HashSet<Pair<Int, Set<Int>>>()


    fun dominanceTree(node: Node): Set<Pair<Int, Int>> {
//        this.stack.add(node)
//        if(node.inEdges.size == 1) {
//            idoms.add(Pair(node.inEdges.first().node!!.id, node.id))
//        }
//            for (edge in node.outEdges) {
//                if (edge.node == null) {
//                    continue
//                }
//
//                if (!this.stack.contains(edge.node)) {
//                    this.dominanceTree(edge.node!!)
//                }
//            }
        remarkIds(node)
//        val dt = this.mapToDT(node)
        node.resetColors()
        this.run(node)
        node.resetColors()
        return this.mapIdoms(node)
    }

    private fun mapIdoms(node: Node): Set<Pair<Int, Int>> {
        val result = HashSet<Pair<Int, Int>>()
        fun f(node: Node) {
            if (node.color == Node.Color.GREY) {
                return
            }
            node.color = Node.Color.GREY

            val idom = node.doms.filter { it.id != node.id }.minBy { it.id }
            if(idom != null) {
                result.add(Pair(idom.id, node.id))
            }

            node.children().forEach { f(it) }
            node.color = Node.Color.BLACK
        }
        f(node)
        return result
    }

    private fun run(node: Node) {
        if (node.color == Node.Color.GREY) {
            return
        }
        node.color = Node.Color.GREY
        val doms = node.fwParents().map { it.doms }
        if (doms.isEmpty()) {
            node.doms = setOf(node)
        } else {
            node.doms = doms.reduce({ acc, d -> acc.intersect(d) }) + node
        }

        node.children().forEach { run(it) }
        node.color = Node.Color.BLACK
    }
//    private fun run(node: Node) {
//        fun r(node: Node) {
//            node.color = Node.Color.GREY
//            for (n in node.children()) {
//                if(n.color == Node.Color.GREY) {
//                    continue
//                }
//                r(n)
//            }
//            val l = ArrayList<Int>()
//            val parents = node.parents()
//            if(parents.isNotEmpty()) {
//                val v = node.inEdges.first().node?.id
//                if (v != null) {
//                    l.add(v)
//                }
//            }
//            val doms = node.inEdges.stream().mapIdoms { it.node?.doms }.reduce(l, {acc, doms ->
//                if(acc == null || doms == null) {
//                    return@reduce acc
//                }
//                acc.intersect(doms).toList()
//            })
//            if(doms != null) {
//                node.doms = doms + node.id
//            } else {
//                node.doms = listOf(node.id)
//            }
//            node.color = Node.Color.BLACK
//        }
//        r(node)
//    }

//    private fun mapToDT(node: Node): DT {
//        fun toDT(node: Node, path: List<Int>, parent: DT?): DT {
//            val id = node.id
//            node.color = Node.Color.GREY
//            val children = ArrayList<DT>()
//            val doms = path + id
//            val l = ArrayList<DT>()
//            if(parent != null) {
//                l.add(parent)
//            }
//            val dt = DT(id, children, doms, l)
//            for (edge in node.outEdges) {
//                if (edge.node == null) {
//                    continue
//                }
//                if(edge.node?.color == Node.Color.GREY) {
//                    continue
//                }
//                val child = toDT(edge.node!!, path + id, dt)
//                children.add(child)
//            }
//            node.color = Node.Color.BLACK
//            return dt
//        }
//        return toDT(node, ArrayList(), null)
//    }


}

fun remarkIds(node: Node) {
    var index = 0
    fun remark(node: Node) {
        node.color = Node.Color.GREY
        for (edge in node.outEdges) {
            if (edge.node == null) {
                continue
            }
            if (edge.node?.color == Node.Color.GREY) {
                continue
            }
            remark(edge.node!!)
        }
        node.color = Node.Color.BLACK
        node.id = index
        index++
    }
    remark(node)
}

//data class DT (val id: Int, val children: List<DT>, var doms: List<Int>, val parents: List<DT>) {
//    var color: DT.Color = Color.WHITE
//
//    enum class Color {
//        WHITE, GREY, BLACK
//    }
//}
