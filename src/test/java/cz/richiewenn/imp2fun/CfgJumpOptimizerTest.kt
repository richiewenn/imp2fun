package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.expressions.JumpExpr
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CfgJumpOptimizerTest {

    @Test
    fun `Optimize simple graph`() {
        val graph = CfgInEdgesFiller().fill(Node(1, listOf(Edge(Node(2, listOf(Edge(Node(3), JumpExpr())))))))

        val optimizedGraph = CfgJumpOptimizer().optimize(graph)

        assertEquals(3, optimizedGraph.outEdges.first().node?.id)
    }
}