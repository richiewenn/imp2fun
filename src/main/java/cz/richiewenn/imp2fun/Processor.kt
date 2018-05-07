package cz.richiewenn.imp2fun

import com.github.javaparser.JavaParser
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.haskell.ast.Ast
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory.graph
import java.io.File
import com.github.javaparser.ast.Node as AstNode

val astPreprocessor: (String) -> AstNode = {
    val cu = JavaParser.parse(it)
    val method = cu.findRootNode().childNodes[0].childNodes[1]
    val methodBody = method.childNodes.last()
    methodBody
}
val cfgPreprocessor: (AstNode) -> Node = {
    Ast2Cfg.toCFG(it)
}
val fillInEdges: (Node) -> (Node) = {
    CfgInEdgesFiller().fill(it)
}
val removeJumps: (Node) -> (Node) = {
    CfgJumpOptimizer().optimize(it)
}
val dominanceFrontiers: (Node) -> (Node) = {
    DominanceFrontiers.fill(it)
}
val insertPhiFunctions: (Node) -> (Node) = {
    RonCytronsPhiFiller().insertPhiFunctions(it)
}
val renameVariables: (Node) -> (Node) = {
    RonCytronsPhiFiller().renameVariables(it)
}
val phiFunctionsOptimizer: (Node) -> (Node) = {
    PhiFunctionOptimizer().optimize(it)
}
val printDot: (Node) -> (Node) = {
    println("---------")
    println(DotConverter().convert(it).joinToString(System.lineSeparator()))
    it
}
val convertToHaskellAst: (Node) -> Ast = {
//    HaskellAstConverter.convertV2(it)
    HaskellAstConverter.convert(it)
}
val printAstDot: (Ast) -> (Ast) = {
    println("---------")
//    val graph = DotConverter().convertToGraph(it)
    val link = it.getDotLinkSources()
//    val links = listOf(node("1").link(node("2"), node("3")))
    val graph = graph().directed().with(link)
    Graphviz.fromGraph(graph).width(1800).render(Format.SVG).toFile(File("./graph.svg"))
    it
}

private operator fun Node.plus(f: (Node) -> Node): Node {
    return f(this)
}
private operator fun AstNode.plus(f: (AstNode) -> Node): Node {
    return f(this)
}
private operator fun Node.plus(f: (Node) -> Ast): Ast {
    return f(this)
}
private operator fun Ast.plus(f: (Ast) -> Ast): Ast {
    return f(this)
}

fun main(args: Array<String>) {
    val code = """public class Prime {
    public int prime() {
        int a = 0;
        for (int i = 0; i < 10; i = i + 1) {
            if(i == 9) {
                a = 1;
            }
        }
        return a;
    }
}"""

    val result = astPreprocessor(code) +
        cfgPreprocessor +
        fillInEdges +
        removeJumps +
        printDot +
        dominanceFrontiers +
        insertPhiFunctions +
        phiFunctionsOptimizer +
        renameVariables +
        printDot +
        convertToHaskellAst +
        printAstDot

    println("-----------------------")
    println(result.printCode())
}

// Argumenty funkci jsou veci v phi funkcich
