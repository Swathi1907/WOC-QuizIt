object PromptBuilder {

    fun buildPrompt(content: String, count: Int): String {
        return """
You are a quiz generator.

Return ONLY raw JSON.
DO NOT wrap in ```json
DO NOT add any text outside JSON.

JSON FORMAT (STRICT — DO NOT CHANGE KEYS):

{
  "questions": [
    {
      "number": 1,
      "question": "Question text",
      "options": ["Option A", "Option B", "Option C", "Option D"],
      "correctAnswer": "A",
      "explanation": "Single line explanation"
    }
  ]
}

RULES:
- Generate EXACTLY $count questions
- Every question MUST have exactly 4 options
- correctAnswer MUST be A, B, C, or D
- explanation MUST be SINGLE LINE
- Use ONLY the content below
- Do NOT add commentary
- Do NOT apologize

CONTENT:
$content
        """.trimIndent()
    }
}