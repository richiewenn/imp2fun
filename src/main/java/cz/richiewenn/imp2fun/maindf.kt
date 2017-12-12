package cz.richiewenn.imp2fun

import cz.richiewenn.imp2fun.cfg.Edge
import cz.richiewenn.imp2fun.cfg.Node
import com.github.javaparser.ast.Node as AstNode

fun main(args: Array<String>) {

    // https://courses.cs.washington.edu/courses/csep501/14sp/video/archive/html5/video.html?id=csep501_14sp_8
    val n13 = Node(13)
    val n12 = Node(12, n13)
    val n4 = Node(4, n13)
    val n8 = Node(8, n13)
    val n5 = Node(5,
        Node(6, n4, n8),
        Node(7, n12, n8))
    n8.outEdges = n8.outEdges + Edge(n5)
    val cfg = Node(1,
        Node(2, Node(3, n4)),
        n5,
        Node(9, Node(10, n12), Node(11, n12))
    )


    val filledCfg = CfgInEdgesFiller().fill(cfg)
//    println(DotConverter().convert(filledCfg).joinToString(System.lineSeparator()))
//    println("--------------------")
    val dom = DominanceTree().dominanceTree(filledCfg)
    println(dom.map { "${it.first}->${it.second}" }.joinToString(System.lineSeparator()))
    println("--------------------")
//    println(DotConverter().convert(filledCfg).joinToString(System.lineSeparator()))
    val frontiers = DominanceFrontiers.calculate(filledCfg)
    val withFrontiers = DominanceFrontiers.fill(filledCfg)
//    println("--------------------")
//    println(frontiers.map { it.id })
    println(DotConverter().convert(withFrontiers).joinToString(System.lineSeparator()))

}


