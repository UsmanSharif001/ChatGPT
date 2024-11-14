package com.example.chatgptus.controller;

import com.example.chatgptus.dto.ChatRequest;
import com.example.chatgptus.dto.ChatResponse;
import com.example.chatgptus.dto.Choice;
import com.example.chatgptus.dto.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.concurrent.TimeUnit;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatGPTController {

    private final WebClient webClient;

    @Autowired
    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping("/getTrivia")
    public ResponseEntity<Map<String, Object>> getTrivia(@RequestParam String category) {
        System.out.println("Received category: " + category);

        try {
            // Step 1: Fetch trivia question from Open Trivia API
            Map<String, Object> triviaResponse = getTriviaQuestion(category);
            if (triviaResponse.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(triviaResponse);
            }

            String question = (String) triviaResponse.get("question");
            String correctAnswer = (String) triviaResponse.get("correct_answer");

            // Step 2: Use ChatGPT to generate multiple-choice options
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setModel("gpt-3.5-turbo");

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", "You are a quiz generator assistant."));
            messages.add(new Message("user", "Generate 4 multiple-choice answers for the following trivia question. "
                    + "Make sure one of the options is the correct answer."
                    + "\nQuestion: " + question + "\nCorrect answer: " + correctAnswer));

            chatRequest.setMessages(messages);
            chatRequest.setN(1);
            chatRequest.setTemperature(1);
            chatRequest.setMaxTokens(150);

            // Call ChatGPT to get the multiple-choice options
            ChatResponse aiResponse = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth("NotTheRealKey"))
                    .bodyValue(chatRequest)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            if (aiResponse == null || aiResponse.getChoices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Failed to generate AI multiple-choice options"));
            }

            // Step 3: Extract the generated options from ChatGPT response
            String aiGeneratedChoices = aiResponse.getChoices().get(0).getMessage().getContent().trim();

            // Split the generated choices into an array (assuming they are separated by newlines or commas)
            String[] choicesArray = aiGeneratedChoices.split("\n|,");  // Split by either newline or comma

            // Prepare the result to send back to the frontend
            Map<String, Object> result = new HashMap<>();
            result.put("question", question);
            result.put("choices", Arrays.asList(choicesArray));  // Return the choices as an array

            System.out.println("AI-generated trivia data sent to client: " + result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal server error"));
        }
    }

    // Method to get the trivia question based on category
    public Map<String, Object> getTriviaQuestion(String category) {
        int retries = 5;
        int delay = 4000;

        // Adjust triviaApiUrl to use the category from the request
        String triviaApiUrl = "https://opentdb.com/api.php?amount=1&type=multiple&category=" + category;
        System.out.println("Calling Trivia API: " + triviaApiUrl);

        while (retries > 0) {
            try {
                WebClient client = WebClient.create();
                String triviaResponse = client.get()
                        .uri(triviaApiUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JSONObject jsonResponse = new JSONObject(triviaResponse);
                int responseCode = jsonResponse.getInt("response_code");

                if (responseCode == 0) {
                    JSONObject questionData = jsonResponse.getJSONArray("results").getJSONObject(0);
                    String question = questionData.getString("question");
                    String correctAnswer = questionData.getString("correct_answer");

                    Map<String, Object> result = new HashMap<>();
                    result.put("question", question);
                    result.put("correct_answer", correctAnswer);

                    return result;
                } else {
                    return Collections.singletonMap("error", "Invalid trivia category or request");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Collections.singletonMap("error", "Error fetching trivia question");
            }
        }

        return Collections.singletonMap("error", "Max retries reached. Try again later.");
    }
}
