object PromptBuilder {

    fun buildPrompt(
        content: String,
        count: Int,
        difficulty: String
    ): String {
        return """
You are an AI quiz generator.

Your task is to generate a quiz STRICTLY based on the provided study material.

STUDY MATERIAL:
$content

QUIZ SETTINGS:
- Number of questions: $count
- Difficulty level: $difficulty

DIFFICULTY RULES:
- Easy: Direct factual questions, simple concepts
- Medium: Concept-based, requires understanding
- Hard: Analytical, tricky, multi-step reasoning

OUTPUT RULES (VERY IMPORTANT):
- Return ONLY valid raw JSON
- DO NOT add explanations outside JSON
- DO NOT wrap JSON in ```json
- DO NOT include any extra text
- JSON must be parseable

JSON FORMAT (DO NOT CHANGE KEYS):
{
  "questions": [
    {
      "number": 1,
      "question": "Question text",
      "options": [
        "Option A",
        "Option B",
        "Option C",
        "Option D"
      ],
      "correctAnswer": "A",
      "explanation": "Single line explanation"
    }
  ]
}

STRICT RULES:
- Generate EXACTLY $count questions
- Each question MUST have exactly 4 options
- correctAnswer MUST be one of: A, B, C, D
- explanation MUST be ONE LINE
- Questions MUST be derived ONLY from the study material
- No repeated questions
- Maintain the chosen difficulty level strictly

Now generate the quiz.
""".trimIndent()
    }
}