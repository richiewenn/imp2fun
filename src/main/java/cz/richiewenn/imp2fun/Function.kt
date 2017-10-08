package cz.richiewenn.imp2fun

import com.github.javaparser.ast.Node

data class Function(
    private val origNode: Node
) {
    val name = this.origNode.childNodes[0]
    val returnType = this.origNode.childNodes[this.origNode.childNodes.size - 2]
    val params = this.origNode.childNodes.subList(1, this.origNode.childNodes.size - 2)
    val body = this.origNode.childNodes.last()


}