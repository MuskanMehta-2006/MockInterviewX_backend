package com.example.interview_ai.demo.controller;

import com.example.interview_ai.demo.dto.*;
import com.example.interview_ai.demo.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/interview")
public class AIInterviewController {

    private final AIService aiService;

    public AIInterviewController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/question")
    public ResponseEntity<?> getQuestion(
            @RequestParam String type,
            @RequestParam String level
    ) {
        System.out.println("Controller hit -> type: " + type + ", level: " + level);

        String normalizedType = type.trim().toUpperCase();

        if (normalizedType.equals("DSA")) {
            return ResponseEntity.ok(aiService.generateQuestion(type, level));
        }

        if (normalizedType.equals("AIML") || normalizedType.equals("AI") || normalizedType.equals("AI-ML")) {
            return ResponseEntity.ok(aiService.generateAIMLQuestion(type, level));
        }

        if (normalizedType.equals("DB") ||
                normalizedType.equals("DBMS") ||
                normalizedType.equals("SQL") ||
                normalizedType.equals("DATABASE")) {

            return ResponseEntity.ok(
                    aiService.generateDBQuestion(type, level)
            );
        }

        return ResponseEntity.badRequest()
                .body("Invalid type: " + type + " | Allowed: DSA, AIML, DBMS");
    }

    @PostMapping("/evaluate")
    public String evaluate(@RequestBody AIAnswerRequest request) {

        return aiService.evaluateAnswer(
                request.getQuestion(),
                request.getCode() + "\nMODE:" + request.getMode(),
                request.getLanguage()   // 👈 ADD THIS
        );
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackResult> getFeedback(@RequestBody AIAnswerRequest request) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            String feedbackStr = aiService.generateFeedback(
                    request.getQuestion(),
                    request.getCode(),
                    request.getLanguage()
            );

            System.out.println("RAW FEEDBACK: " + feedbackStr);

            // 🔥 STEP 1: remove ```json ```
            String cleaned = feedbackStr
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // 🔥 STEP 2: extract only JSON (extra safety)
            int start = cleaned.indexOf("{");
            int end = cleaned.lastIndexOf("}");

            if (start != -1 && end != -1) {
                cleaned = cleaned.substring(start, end + 1);
            }

            System.out.println("CLEANED FEEDBACK: " + cleaned);

            // 🔥 STEP 3: parse
            FeedbackResult feedback =
                    mapper.readValue(cleaned, FeedbackResult.class);

            return ResponseEntity.ok(feedback);

        } catch (Exception e) {
            e.printStackTrace();

            // 🔥 fallback (VERY IMPORTANT)
            FeedbackResult fallback = new FeedbackResult();
            fallback.setTimeComplexity("-");
            fallback.setSpaceComplexity("-");
            fallback.setCodeQuality("Error");
            fallback.setScore(0);
            fallback.setFeedback("AI parsing failed");

            return ResponseEntity.ok(fallback);
        }
    }

    @PostMapping("/theoryQuestions")
    public ResponseEntity<?> getTheoryQuestions(
            @RequestBody Map<String, String> body
    ) {
        String type = body.get("type");
        String level = body.get("level");

        System.out.println("AI Theory Questions -> type: " + type + ", level: " + level);

        return ResponseEntity.ok(
                aiService.generateTheoryQuestions(type, level)
        );
    }
    @PostMapping("/theory-feedback")
    public ResponseEntity<TheoryFeedbackResponse> evaluateTheory(
            @RequestBody TheoryFeedbackRequest request
    ) {
        System.out.println("EVALUVATING....");
        return ResponseEntity.ok(aiService.evaluateTheory(request));
    }
    @PostMapping("/hint")
    public ResponseEntity<?> getHint(@RequestBody AIAnswerRequest request) {

        System.out.println("Hint API hit");

        String hint = aiService.generateHint(
                request.getQuestion(),
                request.getCode(),
                request.getLanguage()
        );

        return ResponseEntity.ok(Map.of("hint", hint));
    }
}
