package cz.richiewenn.imp2fun

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.Node as AstNode

fun main(args: Array<String>) {
    val fibonacci = """
public class Fibonacci {

    public int[] fibonacci(int count, int count2) {
        int[] feb = new int[count];
        feb[0] = 0;
        feb[1] = 1;
        for(int i=2; i < count; i++){
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
        for(int num=1; count<N; num++) {
            for(i=2; num%i != 0; i++);

            if(i == num) {
                primes[count] = num;
                count++;
            }
        }
        return primes;
    }
}
        """.trimIndent()

//    val cu = JavaParser.parse(fibonacci)
    val cu = JavaParser.parse(primes)
    val method = cu.findRootNode().childNodes[0].childNodes[1]
    val methodName = method.childNodes[0]
    val methodBody = method.childNodes.last()
    val methodReturnType = method.childNodes[method.childNodes.size - 2]
    val methodParams = method.childNodes.subList(1, method.childNodes.size - 2)

    val cfg = Ast2Cfg.toCFG(methodBody)
    val result1 = DotConverter().convert(cfg).joinToString(System.lineSeparator())
    val filledCfg = CfgInEdgesFiller().fill(cfg)
    val optimizedCfg = CfgJumpOptimizer().optimize(filledCfg)
    val result2 = DotConverter().convert(optimizedCfg).joinToString(System.lineSeparator())
    println(result1)
    println("--------------------")
    println(result2)
}


