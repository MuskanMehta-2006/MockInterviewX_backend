package com.example.interview_ai.demo.service;

import com.example.interview_ai.demo.dto.AIQuestionResponse;
import com.example.interview_ai.demo.dto.TheoryFeedbackRequest;
import com.example.interview_ai.demo.dto.TheoryFeedbackResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.*;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String API_KEY;

    @Value("${ai.api.url}")
    private String API_URL;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ================================
    // 🎯 GENERATE QUESTION
    // ================================
    public AIQuestionResponse generateQuestion(String type, String level) {
        System.out.println("in service");
        String prompt =
                "Generate ONE DSA interview question in STRICT JSON format.\n" +
                        "Rules:\n" +
                        "- Only return raw JSON (NO markdown, NO ```)\n" +
                        "- Exactly 2 examples\n" +
                        "- Must include constraints\n" +
                        "- Level: " + level + "\n\n" +
                        "Format:\n" +
                        "{\n" +
                        "  \"question\": \"\",\n" +
                        "  \"statement\": \"\",\n" +
                        "  \"example1\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"example2\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"constraints\": [\"\", \"\"]\n" +
                        "}";

        String aiResponse = callAI(prompt);

        // 🔥 CLEAN RESPONSE (IMPORTANT FIX)
        String clean = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            System.out.println(mapper.readValue(clean, AIQuestionResponse.class));
            return mapper.readValue(clean, AIQuestionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("AI JSON parsing failed: " + clean, e);
        }
    }
    public AIQuestionResponse generateAIMLQuestion(String type, String level) {

        System.out.println("in AIML service");

        String prompt =
                "Generate ONE AI/ML interview question in STRICT JSON format.\n" +
                        "Rules:\n" +
                        "- Only return raw JSON (NO markdown, NO ```)\n" +
                        "- Question must be related to AI/ML concepts OR ML coding (Python preferred)\n" +
                        "- Include real-world ML scenario (like dataset, model, prediction, etc.)\n" +
                        "- Exactly 2 examples\n" +
                        "- Must include constraints\n" +
                        "- Level: " + level + "\n\n" +

                        "Focus areas:\n" +
                        "- Machine Learning (regression, classification)\n" +
                        "- Data preprocessing\n" +
                        "- NumPy / Pandas problems\n" +
                        "- Simple ML logic / pseudo training\n\n" +

                        "Format:\n" +
                        "{\n" +
                        "  \"question\": \"\",\n" +
                        "  \"statement\": \"\",\n" +
                        "  \"example1\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"example2\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"constraints\": [\"\", \"\"]\n" +
                        "}";

        String aiResponse = callAI(prompt);

        // 🔥 CLEAN RESPONSE
        String clean = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            System.out.println(mapper.readValue(clean, AIQuestionResponse.class));
            return mapper.readValue(clean, AIQuestionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("AIML JSON parsing failed: " + clean, e);
        }
    }
    public AIQuestionResponse generateDBQuestion(String type, String level) {

        System.out.println("in AIML service");

        String prompt =
                "Generate ONE SQL-based AI/ML interview question in STRICT JSON format.\n\n" +

                        "Rules:\n" +
                        "- Only return raw JSON (NO markdown, NO ```)\n" +
                        "- The question MUST be a SQL query problem\n" +
                        "- Focus on database operations used in ML pipelines (feature extraction, aggregation, filtering, joins)\n" +
                        "- Real-world scenario should involve data like customers, sales, transactions, logs, or predictions\n" +
                        "- Exactly 2 examples (input-output style)\n" +
                        "- Must include constraints\n" +
                        "- Level: " + level + "\n\n" +

                        "Focus areas:\n" +
                        "- SQL queries for ML datasets\n" +
                        "- Aggregations (AVG, SUM, COUNT)\n" +
                        "- Joins between tables\n" +
                        "- Filtering based on conditions\n" +
                        "- Feature engineering using SQL\n\n" +

                        "Format:\n" +
                        "{\n" +
                        "  \"question\": \"\",\n" +
                        "  \"statement\": \"Write an SQL query ...\",\n" +
                        "  \"example1\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"example2\": { \"input\": \"\", \"output\": \"\" },\n" +
                        "  \"constraints\": [\"\", \"\"]\n" +
                        "}";
        String aiResponse = callAI(prompt);

        // 🔥 CLEAN RESPONSE
        String clean = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            System.out.println(mapper.readValue(clean, AIQuestionResponse.class));
            return mapper.readValue(clean, AIQuestionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("AIML JSON parsing failed: " + clean, e);
        }
    }



    // ================================
    // 🧠 EVALUATE ANSWER
    public String evaluateAnswer(String question, String code, String language) {
        System.out.println(code);
        String prompt =
                "You are a STRICT ONLINE JUDGE like LeetCode.\n" +

                        "================ INPUTS ================\n" +
                        "LANGUAGE:\n" + language + "\n\n" +
                        "QUESTION:\n" + question + "\n\n" +
                        "USER CODE:\n" + code + "\n\n" +

                        "================ STRICT RULES ================\n" +
                        "1. Use ONLY given language: " + language + "\n" +
                        "2. DO NOT convert language\n" +
                        "3. DO NOT fix or complete code\n" +
                        "4. If code incomplete → COMPILE_ERROR\n" +
                        "5. If syntax wrong → COMPILE_ERROR\n" +
                        "6. If runtime crash possible → RUNTIME_ERROR\n" +
                        "7. If logic incorrect → WRONG_ANSWER\n\n" +

                        "================ EXECUTION ================\n" +
                        "- Simulate execution on multiple test cases\n" +
                        "- Include edge cases:\n" +
                        "  empty input, single element, duplicates, negatives, large input\n\n" +

                        "================ FINAL OUTPUT (STRICT) ================\n" +
                        "Return EXACTLY in this format:\n" +
                        "{\n" +
                        "  \"status\": \"COMPILE_ERROR | RUNTIME_ERROR | WRONG_ANSWER | PARTIAL_PASS | ACCEPTED\",\n" +
                        "  \"passed\": true/false,\n" +
                        "  \"message\": \"short reason (max 1 line)\"\n" +
                        "}\n\n" +

                        "================ IMPORTANT ================\n" +
                        "- NO explanation\n" +
                        "- NO code suggestion\n" +
                        "- ONLY JSON output\n";
        return callAI(prompt);
    }


    public String generateFeedback(String question, String code, String language) {
        System.out.println(code);
        String prompt =
                "You are a STRICT code evaluator used in an online coding platform like LeetCode.\n\n" +

                        "LANGUAGE:\n" + language + "\n\n" +
                        "QUESTION:\n" + question + "\n\n" +
                        "CODE:\n" + code + "\n\n" +

                        "CRITICAL RULE (VERY IMPORTANT):\n" +
                        "Before doing ANY analysis, first check if the code is valid programming code.\n" +
                        "Valid code means:\n" +
                        "- It must contain meaningful programming syntax\n" +
                        "- It must not be random characters or gibberish\n" +
                        "- It must be related to the question\n\n" +

                        "IF CODE IS INVALID / RANDOM / NON-CODE:\n" +
                        "- timeComplexity = \"\"\n" +
                        "- spaceComplexity = \"\"\n" +
                        "- codeQuality = \"Poor\"\n" +
                        "- score = 0\n" +
                        "- feedback = \"Invalid or non-compilable / meaningless code. Please write proper solution.\"\n" +
                        "STOP HERE. DO NOT DO ANY FURTHER ANALYSIS.\n\n" +

                        "IF CODE IS VALID:\n" +
                        "Step 1: Classify PASS or FAIL logically\n\n" +

                        "PASS:\n" +
                        "- timeComplexity\n" +
                        "- spaceComplexity\n" +
                        "- codeQuality (Bad/Average/Good/Excellent)\n" +
                        "- score (0-100)\n" +
                        "- feedback: 1-2 lines improvement suggestion only\n\n" +

                        "FAIL (but valid code):\n" +
                        "- Identify logic issues\n" +
                        "- Give hint or correction (max 2 lines)\n" +
                        "- DO NOT give TC/SC\n" +
                        "- score must be <= 40\n\n" +

                        "STRICT OUTPUT FORMAT:\n" +
                        "Return ONLY valid JSON. No markdown, no explanation.\n" +
                        "{\n" +
                        "  \"timeComplexity\": \"\",\n" +
                        "  \"spaceComplexity\": \"\",\n" +
                        "  \"codeQuality\": \"\",\n" +
                        "  \"score\": 0,\n" +
                        "  \"feedback\": \"\"\n" +
                        "}";

        return callAI(prompt);
    }

    public List<String> generateTheoryQuestions(String type, String level) {

        if (type == null) type = "fundamentals";
        if (level == null) level = "easy";

        String prompt =
                "You are an expert interview question generator.\n" +
                        "Generate exactly 10 HIGH-QUALITY interview theory questions.\n\n" +

                        "RULES:\n" +
                        "- Questions must STRICTLY belong to the given domain only\n" +
                        "- Do NOT mix topics from other domains\n" +
                        "- Return ONLY a JSON array of strings\n" +
                        "- No explanation, no numbering, no markdown\n\n" +

                        "DOMAIN RULES:\n" +
                        "- If type = fundamentals → include OOPS, DBMS, OS, CN basics (balanced mix)\n" +
                        "- If type = web → include HTML, CSS, JavaScript, React, Tailwind, frontend concepts only\n" +
                        "- If type = dsa → include algorithms, arrays, trees, sorting, complexity\n" +
                        "- If type = dbms → include SQL, normalization, joins, indexing, transactions\n" +
                        "- If type = ai-ml → include ML concepts, models, training, evaluation\n\n" +

                        "LEVEL: " + level + "\n" +
                        "TYPE: " + type + "\n\n" +

                        "OUTPUT FORMAT:\n" +
                        "[\"question1\", \"question2\", ...]";
        String aiResponse = callAI(prompt);

        try {
            // clean response
            String clean = aiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return mapper.readValue(clean, List.class);

        } catch (Exception e) {
            throw new RuntimeException("Theory AI parsing failed: " + aiResponse, e);
        }
    }
    // ================================
    // 🔥 OPENROUTER API CALL
    // ================================
    private String callAI(String prompt) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "openrouter/auto");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            body.put("messages", List.of(message));

            String json = mapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "http://localhost:8080")
                    .addHeader("X-Title", "AI Interview App")
                    .post(RequestBody.create(json, MediaType.get("application/json")))
                    .build();

            Response response = client.newCall(request).execute();

            if (response.body() == null) return "AI Error: Empty response";

            String res = response.body().string();

            System.out.println("RAW RESPONSE: " + res);

            Map map = mapper.readValue(res, Map.class);

            if (map.containsKey("choices")) {
                List choices = (List) map.get("choices");

                if (!choices.isEmpty()) {
                    Map first = (Map) choices.get(0);
                    Map msg = (Map) first.get("message");

                    return (String) msg.get("content");
                }
            }

            return "Unexpected AI format: " + res;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI Error: " + e.getMessage();
        }
    }

    public TheoryFeedbackResponse evaluateTheory(TheoryFeedbackRequest request) {

        String prompt =
                "You are an expert interview evaluator.\n\n" +

                        "TASK:\n" +
                        "Evaluate each answer corresponding to its question.\n\n" +

                        "VERY STRICT RULES:\n" +
                        "1. Evaluate answers ONE BY ONE in order\n" +
                        "2. Each answer MUST match the same index question\n" +
                        "3. If answer is empty, null, or irrelevant → score = 0\n" +
                        "4. If answer is correct or mostly correct → score = 1\n" +
                        "5. DO NOT randomly generate scores\n" +
                        "6. Output scores array length MUST be exactly equal to number of questions\n" +
                        "7. Maintain order strictly (Q1→A1, Q2→A2, ...)\n\n" +

                        "SCORING:\n" +
                        "- score = (number of correct answers / total questions) * 100\n\n" +

                        "INPUT DATA:\n" +
                        "TYPE: " + request.getType() + "\n" +
                        "LEVEL: " + request.getLevel() + "\n\n" +

                        "QUESTIONS:\n" + request.getQuestions() + "\n\n" +
                        "ANSWERS:\n" + request.getAnswers() + "\n\n" +

                        "OUTPUT FORMAT (STRICT JSON ONLY):\n" +
                        "{\n" +
                        "  \"score\": 0,\n" +
                        "  \"feedback\": \"short overall feedback\",\n" +
                        "  \"scores\": [0,1,1,0,1,0,1,1,0,1]\n" +
                        "}\n\n" +

                        "IMPORTANT:\n" +
                        "- Do NOT skip any question\n" +
                        "- Do NOT change order\n" +
                        "- Do NOT give extra text\n";

        String aiResponse = callAI(prompt);

        try {
            String clean = aiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return mapper.readValue(clean, TheoryFeedbackResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Theory evaluation failed: " + aiResponse, e);
        }
    }

    public String generateHint(String question, String code, String language) {

        try {
            String prompt =
                    "You are a technical coding interviewer helping a candidate.\n" +
                            "Analyze the problem and the user's code, then give a small helpful hint.\n\n" +

                            "Rules:\n" +
                            "- Do NOT give full solution\n" +
                            "- Do NOT give code\n" +
                            "- Keep the hint within 1-2 lines\n" +
                            "- Be simple and beginner-friendly\n" +
                            "- Hint must be relevant to the given problem\n" +
                            "- Focus on what the user is missing or doing wrong\n\n" +

                            "Problem:\n" + question + "\n\n" +
                            "Programming Language:\n" + language + "\n\n" +
                            "User Code:\n" + code + "\n\n" +

                            "Output:\n" +
                            "Only return the hint text. No JSON. No explanation.";

            String response = callAI(prompt); // 👈 your existing AI call

            if (response == null || response.trim().isEmpty()) {
                return "Try breaking problem into smaller steps.";
            }

            return response.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Hint not available right now";
        }
    }
}