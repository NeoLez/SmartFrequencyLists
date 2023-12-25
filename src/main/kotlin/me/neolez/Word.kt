package me.neolez

data class Word(val word: String, val reading: String, val frequency: Int) {
    override fun toString(): String {
        return "{\"word\":\"" + word + "\"," +
                "\"reading\":\"" + reading + "\"," +
                "\"fq\":" + frequency + "}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word

        if (word != other.word) return false
        if (reading != other.reading) return false

        return true
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + reading.hashCode()
        return result
    }


}