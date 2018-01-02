package cz.richiewenn.imp2fun

import com.github.javaparser.JavaParser
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
    public int[] primes(int N) {
        int[] primes = new int[N];
        int count = 0, max_count = 100, i;
        for(int num=1; count<N; num = num + 1) {
            for(i=2; num%i != 0; i = i + 1);

            if(i == num) {
                primes[count] = num;
                count = count + 1;
            }
        }
        return primes;
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
    }
}
        """.trimIndent()

fun main(args: Array<String>) {

    val cu = JavaParser.parse(simple)
//    val cu = JavaParser.parse(fibonacci)
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

    val filledCfg = CfgInEdgesFiller().fill(cfg)
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
    println(DominanceTree().dominanceTree(filledCfg).map { "${it.first}->${it.second}"}.joinToString(System.lineSeparator()))
    println("--------------------withFrontiers")
    println(result4)
    println("--------------------Dominance Frontiers")
    println(DominanceFrontiers.calculate(filledCfg))
    val phi = PhiFiller.fill(withFrontiers)
    val result5 = DotConverter().convert(phi).joinToString(System.lineSeparator())
    println("--------------------phi")
    println(result5)

}


