package cz.richiewenn.imp2fun

import com.github.javaparser.JavaParser
import cz.richiewenn.imp2fun.cfg.*
import com.github.javaparser.ast.Node as AstNode

fun main(args: Array<String>) {
    val function = """
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
    val primeNums = """
       public class Prime
{
    public int[] primes(int N)
    {
        int[] primes = new int[N];
        int count = 0, max_count = 100, i;
        for(int num=1; count<N; num++)
        {
            //for(i=2; num%i != 0; i++);

            if(i == num)
            {
                primes[count] = num;
                count++;
            }
        }
        return primes;
    }
}
        """.trimIndent()

//    val cu = JavaParser.parse(function)
    val cu = JavaParser.parse(primeNums)
    val method = cu.findRootNode().childNodes[0].childNodes[1]
    val methodName = method.childNodes[0]
    val methodBody = method.childNodes.last()
    val methodReturnType = method.childNodes[method.childNodes.size - 2]
    val methodParams = method.childNodes.subList(1, method.childNodes.size - 2)

    val cfg = toCFG(methodBody)
//    println(cfg)
    println(cfg.toDot())
}

fun toCFG(nodes: List<AstNode>): Node {
    return nodes.map { node ->
         return@map when (node.metaModel.typeName) {
            "MethodDeclaration" -> EndNode()
            "ForStmt" -> forToCFG(node)
            "UnaryExpr", "BinaryExpr", "VariableDeclarationExpr",
            "ExpressionStmt" -> Node(Edge(
                to = EndNode(),
                expression = node.toString())
            )
            "IfStmt" -> ifToCFG(node)
            "BlockStmt" -> toCFG(node.childNodes)
            "ReturnStmt" -> EndNode(node.toString())
            else -> EndNode()
        }
    }.reduce { left, right -> left.plus(right)}
}

fun toCFG(node: AstNode) = toCFG(listOf(node))

fun ifToCFG(node: AstNode): Node {
    val children = node.childNodes
    val inner = toCFG(children[0])
    val body = toCFG(children[1])
    inner.plus(body)
    val condition = ConditionNode(inner.edge, Edge(EndNode(), "OTHERWISE")) // otherwise)
    inner.plus(Node(JumpEdge(condition.edge.to!!.id)))
    return condition
}

fun forToCFG(node: AstNode): Node {
    val children = node.childNodes
    val defI = toCFG(children[0])
    val astCondition = children[1]
    val ipp = toCFG(children[2])
    val body = toCFG(children[3]).plus(ipp)
    val condition = ConditionNode(
        inner = Edge(body, astCondition.toString()),
        otherwise = Edge(EndNode(), "OTHERWISE") // otherwise
    )
    body.last().edge = JumpEdge(condition.id)
    defI.edge.to = condition
    return defI
}

