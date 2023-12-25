package me.neolez


class WordComparator : Comparator<Word> {
    override fun compare(o1: Word, o2: Word): Int {
        return o1.frequency - o2.frequency
    }
}
