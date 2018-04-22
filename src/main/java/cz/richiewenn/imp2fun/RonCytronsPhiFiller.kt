package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.PhiExpressions
import cz.richiewenn.imp2fun.expressions.VarAssignExpr
import cz.richiewenn.imp2fun.expressions.VarDefExpr
import java.util.*
import kotlin.collections.HashMap

class RonCytronsPhiFiller {
    /**  */
    private val s = HashMap<String, Stack<Int>>()
    /** Set of integers, one for each variable V, the value says how many assignments to V have been processed */
    private val c = HashMap<String, Int>()
    private val hasAlready = HashMap<Int, Int>()
    private val work = HashMap<Int, Int>()
    private var w = HashSet<Edge>()

    fun fill(node: Node): Node {
        this.insertPhiFunctions(node)
        depthFirstSearch(node) {
            it.outEdges.forEach {
                it.exp.getVarDefExprs()
                    .filter { !s.containsKey(it.name) }
                    .forEach {
                        s[it.name] = Stack()
                        c[it.name] = 0
                    }
            }
        }
        node.outEdges.forEach { search(it) }
        return node
    }

    private fun insertPhiFunctions(node: Node) {
        depthFirstSearch(node) { n ->
            n.outEdges.forEach { edge ->
                hasAlready[edge.id] = 0
                work[edge.id] = 0
            }
        }
        val frontiers = DominanceFrontiers.calculate(node)
        val sequence = generateSequence(1) { it + 1 }.iterator()
        depthFirstSearch(node) { n ->
            n.outEdges.forEach { edge ->
                edge.exp.getVarDefExprs().forEach { varDef ->
                    val iterCount = sequence.next()
                    work[edge.id] = iterCount
                    w.add(edge)
                }
            }
            val phi = PhiExpressions(w.map { e ->
                val name = e.exp.getVarDefExprs().first().name
                PhiExpression(
                    target = name,
                    originalName = name,
                    vars = getAllRHSVars(name, node)
                )
            })
            val newNode = Node(
                outEdges = listOf(
                    Edge(
                        node = n,
                        exp = phi
                    )
                )
            )
            n.inEdges
                .mapNotNull { it.node }
                .forEach {
                    val out = it.outEdges.find { it.node == n }!!
                    val newEdge = Edge(newNode, out.exp)
                    it.outEdges = it.outEdges.filter { it.node != n } + newEdge
                }
            newNode.inEdges = n.inEdges
            n.inEdges = mutableSetOf(Edge(newNode))

            w = HashSet<Edge>()
        }

    }

    private fun getAllRHSVars(name: String, root: Node): Array<String> {
        val arr = ArrayList<String>()
        depthFirstSearch(root) {
            arr.addAll(it.outEdges
                .flatMap { edge -> edge.exp.getVarUsageExprs() }
                .map { it.variableName }
                .filter { it == name }
            )
        }
        return arr.flatMap { listOf(it, it, it, it, it) }.toTypedArray()
    }

    private fun search(edge: Edge) {
        edge.exp.getVarUsageExprs()
            .filter { it.variableName == getOriginalName(it.variableName) }
            .forEach {
                it.variableName = it.variableName + "_" + this.s[it.variableName]!!.peek()
            }
        edge.exp.getVarDefExprs()
            .filter { it.name == getOriginalName(it.name) }
            .forEach {
                val i = this.c[it.name]!!
                this.s[it.name]!!.push(i)
                this.c[it.name] = i + 1
                it.name = it.name + "_" + i
            }
        if (edge.node != null) {
            depthFirstSearch(edge.node!!) { y ->
                val j = whichPred(edge.node!!, y)
                y.outEdges
                    .filter { it.exp is PhiExpressions }
                    .forEach {
                        (it.exp as PhiExpressions).phis.forEach {
                            it.vars[j] = it.vars[j] + "_" + this.s[it.originalName]!!.peek()
                        }
                    }
            }
        }
        if (edge.node != null) {
            edge.node!!.outEdges.forEach(this::search)
        }
//        edge.exp.getVarDefExprs().forEach {
//            val v = getOriginalName(it.name)
//            this.s[v]!!.pop()
//        }
    }

    /**
     * X->.->.->Y  = 3
     */
    private fun whichPred(x: Node, y: Node): Int {
        fun checkDepth(nodes: List<Node>, depth: Int): Int {
            val parents = nodes.flatMap { it.inEdges }.mapNotNull { it.node }
            val found = parents.any { x == it }
            return if (found) {
                depth + 1
            } else {
                checkDepth(parents, depth + 1)
            }
        }
        if (x == y) {
            return 0
        }
        return checkDepth(listOf(y), 0)
    }

}