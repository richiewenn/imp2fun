package cz.richiewenn.imp2fun

import com.github.javaparser.JavaParser
import cz.richiewenn.imp2fun.tests.Simple
import com.github.javaparser.ast.Node as AstNode

val fibonacci = """
public class Fibonacci {

    public int[] fibonacci(int count, int count2) {
        int[] feb = new int[count];
        feb[0] = 0;
        feb[1] = 1;
        for(int i=2; i < count; i = i + 1){
            feb[i] = feb[i-1] + feb[i-2];
        }
        return feb;
    }
}
        """.trimIndent()
val primes = """
public class Prime {
    public int prime() {
        int a = 0;
        for (int i = 0; i < 10; i = i + 1) {
            for (int j = 0; j < 10; j = j + 1) {
                a = i + j;
            }
        }
        return a;
    }
}
        """.trimIndent()

val forCycle = """
public class Prime {
    public int primes() {
        int count = 0;
        for(int i=0; i < 5; i = i + 1) {
            count = count + 1;
        }
        return count;
    }
}
        """.trimIndent()
val simple = """
public class Simple {
    public int simple() {
        int a = 1;
        if(a == 1) {
            a = 2;
        }
        int b = a;
        return b;
    }
}
        """.trimIndent()
val simple2 = """
public class Simple {
    public int simple() {
        int a = 1;
        int c = 1;
        int d = 1;
        if(a == 1) {
            a = 2;
            c = 2;
        }
        int b = a + c;
        return b;
    }
}
"""

fun main(args: Array<String>) {
    val a = Simple().simple()

//    val cu = JavaParser.parse(simple)
    val cu = JavaParser.parse(fibonacci)
//    val cu = JavaParser.parse(primes)
    val method = cu.findRootNode().childNodes[0].childNodes[1]
    val methodName = method.childNodes[0]
    val methodBody = method.childNodes.last()
    val methodReturnType = method.childNodes[method.childNodes.size - 2]
    val methodParams = method.childNodes.subList(1, method.childNodes.size - 2)

    val cfg = Ast2Cfg.toCFG(methodBody)
    remarkIds(cfg)
    val cfgResult = DotConverter().convert(cfg).joinToString(System.lineSeparator())
    println("--------------------cfg")
    println(cfgResult)

    val filledCfg = CfgIncomingEdgesFiller().fill(cfg)
    val filledCfgResult = DotConverter().convert(filledCfg).joinToString(System.lineSeparator())
    println("--------------------filledCfg")
    println(filledCfgResult)

    val optimizedCfg = CfgJumpOptimizer().optimize(filledCfg)
    val optimizedCfgResult = DotConverter().convert(optimizedCfg).joinToString(System.lineSeparator())
    println("--------------------optimizedCfg")
    println(optimizedCfgResult)


//    println(dom.map { "${it.first}->${it.second}"}.joinToString(System.lineSeparator()))

//    val frontiers = DominanceFrontiers.calculate(filledCfg)
//    if(frontiers.isEmpty()) {
//        println("No frontiers")
//    }
//    println(frontiers.map {it.id}.joinToString(", "))

    val withFrontiers = DominanceFrontiers.fill(filledCfg)
    val result4 = DotConverter().convert(withFrontiers).joinToString(System.lineSeparator())
    println("--------------------dominanceTree")
    println(DominatorTree().dominanceTree(filledCfg).map { "${it.first}->${it.second}"}.joinToString(System.lineSeparator()))
    println("--------------------withFrontiers")
    println(result4)
    println("--------------------Dominance Frontiers")
    println(DominanceFrontiers.calculate(filledCfg))
//    val phi = PhiFiller.fill(withFrontiers)
//    val result5 = DotConverter().convert(phi).joinToString(System.lineSeparator())
//    println("--------------------phi")
//    println(result5)

}


