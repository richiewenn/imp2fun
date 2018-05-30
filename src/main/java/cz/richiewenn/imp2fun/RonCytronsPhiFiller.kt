package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.PhiExpression
import cz.richiewenn.imp2fun.expressions.PhiExpressions
import cz.richiewenn.imp2fun.expressions.VarDefExpr
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class RonCytronsPhiFiller {
    /**  */
    private val s = HashMap<String, Stack<Int>>()
    /** Set of integers, one for each variable V, the value says how many assignments to V have been processed */
    private val c = HashMap<String, Int>()
    private val hasAlready = HashMap<Int, Int>()
    private val work = HashMap<Int, Int>()
    private var w: HashSet<Edge> = HashSet()

    fun renameVariables(root: Node): Node {
        depthFirstSearch(root) {
            it.outEdges.forEach {
                it.exp.getVarDefExprs()
                    .filter { !s.containsKey(it.name) }
                    .forEach {
                        s[it.name] = Stack()
                        c[it.name] = 0
                    }
            }
        }

        val dominanceTree = DominatorTree().dominanceNodeTree(root)
        processingQueue.add(dominanceTree)
        while (processingQueue.isNotEmpty()) {
            this.search()
        }

        return root
    }

    fun insertPhiFunctions(node: Node): Node {
        var iterCount = 0
        // For each node (edge) X do
        depthFirstEdgeSearch(node) { x: Edge ->
            hasAlready[x.id] = 0
            work[x.id] = 0
        }


        w = HashSet()
        /** Set of Edges that contains assigment (varDef) to variable [v] */
        fun a(v: String): Set<Edge> {
            val edges = HashSet<Edge>()
            depthFirstEdgeSearch(node) { edge ->
                if (edge.exp.getVarDefExprs().map { it.name }.contains(v)) {
                    edges.add(edge)
                }
            }
            return edges
        }

        // For each variable V do
        val varNames = HashSet<String>()
        depthFirstEdgeSearch(node) { edge -> varNames.addAll(edge.exp.getVarDefExprs().map { it.name }) }
        varNames.forEach { v ->
            iterCount++
            a(v).forEach { x ->
                work[x.id] = iterCount
                w = w.union(listOf(x)).toHashSet()
            }
            while (w.isNotEmpty()) {
                val x = w.first()
                w.remove(x)
                // For each Y in DF(X)
                DominanceFrontiers.calculate(Node(x)).forEach { dfNode ->
                    val dfx = dfNode.inEdges.map { it.node }.mapNotNull { it?.outEdges }.flatMap { it }.filter { it.node == dfNode }
                    dfx.forEach { y: Edge ->
                        if (hasAlready[y.id]!! < iterCount) {
                            placePhiFunctionAt(y, v)
                            hasAlready[y.id] = iterCount
                            if (work[y.id]!! < iterCount) {
                                work[y.id] = iterCount
                                w = w.union(listOf(y)).toHashSet()
                            }
                        }
                    }
                }
            }
        }

        return node
    }

    private fun placePhiFunctionAt(y: Edge, variable: String) {
        // CASE: Some phi function was already inserted
        if(y.node?.outEdges?.size == 1 && y.node?.outEdges?.first()?.exp is PhiExpressions) { // Already inserted Phi
            val phis = y.node?.outEdges?.first()?.exp!! as PhiExpressions
            if(phis.phis.any { it.target.name == variable }) {
                phis.phis.find { it.target.name == variable }!!.vars.add(variable)
                return // Phi for the variable already exists and RHS variable was added
            } else { // There are some phis, but phi for the variable does not exists yet
                phis.phis.add(PhiExpression(
                    target = VarDefExpr(variable),
                    vars = mutableListOf()
                ))
                return
            }
        }

        // CASE: No phi function exists yet
        val newNode = Node(outEdges = y.node!!.outEdges)
        val phiEdge = Edge(
            node = newNode,
            exp = PhiExpressions(
                phis = mutableListOf(
                    PhiExpression(
                        target = VarDefExpr(variable),
                        vars = mutableListOf()
                    )
                )
            )
        )
        y.node!!.outEdges = listOf(phiEdge)
        newNode.inEdges = mutableSetOf(Edge(y.node))

        newNode.outEdges.mapNotNull { it.node }.forEach { node ->
            node.inEdges.filter { it.node == y.node }.forEach { edge ->
                edge.node = newNode
            }
        }
    }

    var processingQueue = ArrayList<DTNode>()
    private fun search() {
        processingQueue.sortBy { it.node.inEdges.size }
        val dtNode = processingQueue.first()
        processingQueue.remove(dtNode)
        dtNode.node.outEdges.forEach { edge ->
            edge.exp.getVarUsageExprs()
                .filter { it.variableName == getOriginalName(it.variableName) }
                .forEach {
                    if (this.s[it.variableName] != null && this.s[it.variableName]?.isNotEmpty() == true) {
                        it.variableName = it.variableName + "_" + this.s[it.variableName]!!.peek()
                    }
                }
            edge.exp.getVarDefExprs()
                .filter { it.name == getOriginalName(it.name) }
                .forEach {
                    val i = this.c[it.name]!!
                    this.s[it.name]!!.push(i)
                    this.c[it.name] = i + 1
                    it.name = it.name + "_" + i
                }
        } // END of first loop
        dtNode.succ().forEach { (y, depth) ->
            val j = depth // whichPred(edge.node!!, y)
            y.node.outEdges
                .filter { it.exp is PhiExpressions }
                .forEach {
                    (it.exp as PhiExpressions).phis.forEach {
                        if (this.s[it.originalName] != null && this.s[it.originalName]?.isNotEmpty() == true) {
                            it.vars.add(it.originalName + "_" + this.s[it.originalName]!!.peek())
//                            it.vars[j] = it.vars[j] + "_" + this.s[it.originalName]!!.peek()
                        }
                    }
                }
        }
        processingQueue.addAll(dtNode.children)
    }


}