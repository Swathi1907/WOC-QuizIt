object PromptBuilder {

    fun buildPrompt(
        content: String,
        count: Int,
        difficulty: String
    ): String {
        return """
You are an AI quiz generator.

Generate a quiz STRICTLY based on the study material below.

STUDY MATERIAL:
$content

QUIZ SETTINGS:
- Number of questions: $count
- Difficulty: $difficulty

DIFFICULTY RULES:
- Easy: Direct factual questions
- Medium: Concept-based questions
- Hard: Analytical and tricky questions

OUTPUT RULES (MANDATORY):
- Output ONLY valid raw JSON
- No extra text before or after JSON
- No markdown
- JSON must be parsable

IMPORTANT FORMAT RULES:
- Options must NOT contain A., B., C., D.
- Options must be plain text only
- correctAnswer must EXACTLY match one option text
- explanation must be one single line

JSON FORMAT:
{
  "questions": [
    {
      "number": 1,
      "question": "Question text",
      "options": [
        "Option 1",
        "Option 2",
        "Option 3",
        "Option 4"
      ],
      "correctAnswer": "Option 2",
      "explanation": "Short explanation"
    }
  ]
}

STRICT RULES:
- Generate EXACTLY $count questions
- Exactly 4 options per question
- correctAnswer must match one option exactly
- No repeated questions
- Follow difficulty strictly
- Use ONLY the study material

Now generate the quiz.
""".trimIndent()
    }
}