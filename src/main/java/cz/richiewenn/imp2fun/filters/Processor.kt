package cz.richiewenn.imp2fun.filters

import com.github.javaparser.JavaParser
import cz.richiewenn.imp2fun.*
import cz.richiewenn.imp2fun.cfg.Node
import cz.richiewenn.imp2fun.haskell.ast.Ast
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
val phiFunctions: (Node) -> (Node) = {
    PhiFiller.fill(it)
}
val printDot: (Node) -> (Node) = {
    println("---------")
    println(DotConverter().convert(it).joinToString(System.lineSeparator()))
    it
}
val convertToHaskellAst: (Node) -> Ast = {
    HaskellAstConverter.convert(it)
}
val printAstDot: (Ast) -> (Ast) = {
    println("---------")
    println(DotConverter().convert(it).joinToString(System.lineSeparator()))
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
    val result = astPreprocessor(simple) +
    cfgPreprocessor +
//    printDot +
    fillInEdges +
    removeJumps +
    dominanceFrontiers +
    phiFunctions +
    printDot +
    convertToHaskellAst +
    printAstDot

    println("-----------------------")
    println(result.printCode())
}

