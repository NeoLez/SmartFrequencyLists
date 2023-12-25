package me.neolez

import me.neolez.trie.TrieNode
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileWriter
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.Vector
import java.util.function.Function
import java.util.function.Predicate

object Main {

    private var skipHiraganaAndKatakanaChecks: Function<Char, Boolean> = Function { character ->
            val unicodeBlock = Character.UnicodeBlock.of(character)
            unicodeBlock === Character.UnicodeBlock.HIRAGANA || unicodeBlock === Character.UnicodeBlock.KATAKANA || unicodeBlock === Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
        }
    var skipAllChecks: Function<Char, Boolean> = Function { true }
    val removeIfContainsInvalidChar = Predicate<Word> {
        for(c in it.word){
            val unicodeBlock = Character.UnicodeBlock.of(c)
            if (unicodeBlock !== Character.UnicodeBlock.HIRAGANA && unicodeBlock !== Character.UnicodeBlock.KATAKANA && unicodeBlock !== Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION && unicodeBlock !== Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                return@Predicate true
        }
        false
    }
    private var removeIfDoesntContainKanji = Predicate<Word> {
        for (c in it.word) {
            val unicodeBlock = Character.UnicodeBlock.of(c)
            if (unicodeBlock === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                //println(it.word)
                return@Predicate false
            }
        }
        true
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val wordVector = wordVectorFromCSV()

        val trieWords = TrieNode<Word>()
        val trieReading = TrieNode<Word>()

        for (word in wordVector){
            trieWords.addWord(word.word, word)
            trieReading.addWord(word.reading, word)
        }




        val result = trieWords.getAllWordsForCharacters(getKnownCharsArray(), skipHiraganaAndKatakanaChecks)
        result.removeIf(removeIfDoesntContainKanji)

        for (word in result.sortedWith(WordComparator().reversed()))
            println(word)

        println(result.size)
    }

    private fun getKnownCharsArray() : CharArray {

        return String(Main::class.java.classLoader.getResourceAsStream("characters.txt")!!.readAllBytes(), StandardCharsets.UTF_8).toHashSet().toCharArray()
    }

    fun wordVectorFromJSON() : Vector<Word>{
        val vector = Vector<Word>()
        for (i in 1..18) {
            val input = Main::class.java.getResourceAsStream("kanji/term_meta_bank_$i.json")
            val jsonArray = JSONArray(String(input!!.readAllBytes(), StandardCharsets.UTF_8))
            for (nroEntrada in 0..<jsonArray.length()) {
                val arr: JSONArray = jsonArray.getJSONArray(nroEntrada)
                val obj: JSONObject = arr.getJSONObject(2)
                vector.add(Word(arr.getString(0), obj.getString("reading"), obj.getInt("frequency")))
            }
        }
        return vector
    }
    private fun wordVectorFromCSV() : Vector<Word>{
        val wordVector = Vector<Word>()
        val str = String(Main::class.java.classLoader.getResourceAsStream("test.txt")!!.readAllBytes(), StandardCharsets.UTF_8)
        val lines = str.split('\n')
        for(line in lines){
            val fields = line.split(",")
            wordVector.add(Word(fields[0], fields[1], fields[2].toInt()))
        }
        return wordVector
    }

    fun conversion() {
        val strBuilder = StringBuilder()

        for (i in 1..18){
            val input = Main::class.java.getClassLoader().getResourceAsStream("kanji/term_meta_bank_$i.json")
            val jsonArray = JSONArray(String(input!!.readAllBytes(), StandardCharsets.UTF_8))
            for (nroEntrada in 0..<jsonArray.length()) {
                val arr: JSONArray = jsonArray.getJSONArray(nroEntrada)
                val obj: JSONObject = arr.getJSONObject(2)
                val word: String = arr.getString(0)
                val reading: String = obj.getString("reading")
                val freq: Int = obj.getInt("frequency")
                strBuilder.append("$word,$reading,$freq\n")
            }
        }

        val fw = FileWriter("test.txt")
        val str = strBuilder.toString()

        fw.write(str)
        fw.close()
    }
}
