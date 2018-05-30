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
    CfgIncomingEdgesFiller().fill(it)
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
val phiFunctionsArgumentOptimizer: (Node) -> (Node) = {
    PhiFunctionArgumentOptimizer().optimize(it)
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
    val code = """public class Simple {
    public int simple() {
        int a = 0;
        int b = 0;
        for (int i = 0; i < 10; i++) {
            a++;
            if(a % 2 == 0) {
                b++;
            }
        }
        return a + b;
    }
}"""

    val result = astPreprocessor(code) +
        cfgPreprocessor +
//        printDot +
        fillInEdges +
        removeJumps +
//        printDot +
        dominanceFrontiers +
        insertPhiFunctions +
        phiFunctionsOptimizer +
        phiFunctionsArgumentOptimizer +
        renameVariables +
        printDot +
        convertToHaskellAst +
        printAstDot

//    val ast = astPreprocessor(code) +
//        cfgPreprocessor +
//        fillInEdges +
//        removeJumps +
//        dominanceFrontiers +
//        insertPhiFunctions +
//        phiFunctionsOptimizer +
//        phiFunctionsArgumentOptimizer +
//        renameVariables +
//        convertToHaskellAst
//    println(ast.printBeautifulCode())

    println("-----------------------")
    println(result.printCode())
    println("-----------------------")
    println(result.printBeautifulCode())

    Thread.sleep(50) // Just to let prints finish before getting the intellij message to console
}

// Argumenty funkci jsou veci v phi funkcich
