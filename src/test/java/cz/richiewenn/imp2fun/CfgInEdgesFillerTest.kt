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
}