
package com.appdevelopment.project5.parser

data class QuizQuestion(
    val number: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)
//
object RegexQuizParser {

    fun parse(raw: String): List<QuizQuestion> {
        val lines = raw
            .replace("\r", "")
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val results = mutableListOf<QuizQuestion>()
        var i = 0

        while (i < lines.size) {
            if (!lines[i].startsWith("Q")) {
                i++
                continue
            }

            try {
                val number =
                    lines[i].substringAfter("Q").substringBefore(".").trim().toInt()
                val question =
                    lines[i].substringAfter(".").trim()

                val optionA = lines[i + 1].substringAfter("A)").trim()
                val optionB = lines[i + 2].substringAfter("B)").trim()
                val optionC = lines[i + 3].substringAfter("C)").trim()
                val optionD = lines[i + 4].substringAfter("D)").trim()

                val correctAnswer =
                    lines[i + 5].substringAfter(":").trim().first().toString()

                // 🔥 MULTI-LINE EXPLANATION HANDLING
                val explanationBuilder = StringBuilder()
                var j = i + 6

                if (j < lines.size && lines[j].startsWith("Explanation")) {
                    explanationBuilder.append(
                        lines[j].substringAfter("Explanation").replace(":", "").trim()
                    )
                    j++

                    while (j < lines.size && !lines[j].startsWith("Q")) {
                        explanationBuilder.append(" ").append(lines[j])
                        j++
                    }
                }

                results.add(
                    QuizQuestion(
                        number = number,
                        question = question,
                        options = listOf(optionA, optionB, optionC, optionD),
                        correctAnswer = correctAnswer,
                        explanation = explanationBuilder.toString()
                    )
                )

                i = j
            } catch (e: Exception) {
                i++
            }
        }

        return results
    }
}