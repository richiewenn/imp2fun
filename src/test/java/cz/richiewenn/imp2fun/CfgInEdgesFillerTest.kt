package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class CfgInEdgesFillerTest {

    @Test
    fun `Fill simple graph`() {
        val graph = Node(1, Node(2))

        val filledGraph = CfgInEdgesFiller().fill(graph)

        val inEdges = filledGraph.outEdges.first().node?.inEdges

        assertNotNull(inEdges)
        assertEquals(1, inEdges?.size)
        assertEquals(1, inEdges?.first()?.node?.id)
    }

    @Test
    fun `Fill cyclic graph`() {
        val node2 = Node(2)
        val graph = Node(1, node2)
        node2.outEdges = node2.outEdges + Edge(graph)

        val filledGraph = CfgInEdgesFiller().fill(graph)

        assertEquals(1, filledGraph.inEdges.size)
        assertEquals(2, filledGraph.inEdges.first().node?.id)

        val inEdges = filledGraph.outEdges.first().node?.inEdges
        assertNotNull(inEdges)
        assertEquals(1, inEdges?.size)
        assertEquals(1, inEdges?.first()?.node?.id)
    }

    @Test
    fun `Fill graph with more inEdges`() {
        val node4 = Node(4)
        val graph = Node(1, Node(2, node4), Node(3, node4))

        val filledGraph = CfgInEdgesFiller().fill(graph)

        val inEdges = filledGraph.outEdges.first().node?.outEdges?.first()?.node?.inEdges
        assertNotNull(inEdges)
        assertEquals(2, inEdges?.size)
        assertEquals(1, inEdges?.filter { it.node?.id == 2 }?.size)
        assertEquals(1, inEdges?.filter { it.node?.id == 3 }?.size)
    }
}