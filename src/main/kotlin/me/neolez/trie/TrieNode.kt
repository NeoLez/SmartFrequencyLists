package me.neolez.trie

import java.util.function.Function
import kotlin.collections.HashSet


class TrieNode<T> {
    private var value: HashSet<T> = HashSet()
    private var nodes = HashMap<Char, TrieNode<T>>()
    private val isLeaf: Boolean
        get() = nodes.isEmpty()
    private val isEmpty: Boolean
        get() = value.isEmpty()

    fun addWord(s: String, value: T): Boolean {
        if (s.isEmpty()) {
            if (isEmpty) {
                this.value.add(value)
                return true
            }
            this.value.add(value)
            return false
        }

        var node: TrieNode<T>? = nodes[s[0]]
        if (node == null) {
            node = TrieNode()
            nodes[s[0]] = node
        }
        return node.addWord(s.substring(1), value)
    }

    fun getValue(s: String): HashSet<T> {
        if (s.isEmpty())
            return value

        return nodes[s[0]]?.getValue(s.substring(1)) ?: HashSet()
    }

    fun getAllWordsForCharacters(chars: CharArray, skipCharCheckFunction: Function<Char, Boolean>): HashSet<T> {
        val words = HashSet<T>()

        if(!isEmpty)
            words.addAll(value)
        if(isLeaf)
            return words

        val nodesToEvaluate : HashSet<TrieNode<T>> = HashSet()

        chars.forEach {
            val node : TrieNode<T>? = nodes[it]
            if(node!=null)
                nodesToEvaluate.add(node)
        }
        nodes.keys.forEach {
            if(skipCharCheckFunction.apply(it))
                nodesToEvaluate.add(nodes[it]!!)
        }

        nodesToEvaluate.forEach{
            words.addAll(it.getAllWordsForCharacters(chars, skipCharCheckFunction))
        }

        return words
    }

    override fun toString(): String {
        if (isLeaf) return ""
        var s = "["
        for (c in nodes.keys) {
            val n = nodes[c]!!
            s = if (!n.isEmpty) {
                s + "\"" + c + "\"," + n.value.toString() + ",[" + n.toString() + "],"
            } else "$s\"$c\",[$n],"
        }
        return s.substring(0, s.length - 1) + "]"
    }
}
