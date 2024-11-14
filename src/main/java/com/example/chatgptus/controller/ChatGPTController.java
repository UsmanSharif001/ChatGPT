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
            // Fetch trivia question based on category
            Map<String, Object> triviaResponse = getTriviaQuestion(category);  // This returns a Map
            if (triviaResponse.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(triviaResponse);  // Return the error message if present
            }

            String trivia = (String) triviaResponse.get("question");  // Get the question from the Map
            if (trivia == null || trivia.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Trivia question not found"));
            }
            System.out.println("Fetched Trivia: " + trivia);

            // Process the trivia question with GPT
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setModel("gpt-3.5-turbo");

            List<Message> lstMessages = new ArrayList<>();
            lstMessages.add(new Message("system", "You are a helpful assistant."));
            lstMessages.add(new Message("user", "Give me 4 multiple choice answers for the following question: " + trivia));
            chatRequest.setMessages(lstMessages);
            chatRequest.setN(1);  // We only need one response
            chatRequest.setTemperature(1);  // Set reasonable randomness
            chatRequest.setMaxTokens(100);  // Limit the response length

            // Call GPT-3 to generate the multiple choices
            ChatResponse response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth("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"))  // Use your OpenAI token here
                    .bodyValue(chatRequest)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Failed to generate choices"));
            }

            // Extract the choices from the response
            List<Choice> choices = response.getChoices();
            List<String> answerChoices = new ArrayList<>();
            for (Choice choice : choices) {
                // Assuming ChatGPT provides text options for choices
                answerChoices.add(choice.getMessage().getContent().trim());
            }

            // Return the question and choices
            Map<String, Object> result = new HashMap<>();
            result.put("question", trivia);
            result.put("correct_answer", answerChoices.get(0));  // Assuming the first answer is correct
            result.put("incorrect_answers", answerChoices.subList(1, answerChoices.size()));  // Other answers are incorrect
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal server error"));
        }
    }

    // Method to get the trivia question based on category
    public Map<String, Object> getTriviaQuestion(String category) {
        int retries = 5;
        int delay = 2000;

        // Use category ID for sports: 21
        String triviaApiUrl = "https://opentdb.com/api.php?amount=1&type=multiple&category=21";
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
                    List<String> incorrectAnswers = new ArrayList<>();
                    questionData.getJSONArray("incorrect_answers").forEach(ans -> incorrectAnswers.add(ans.toString()));

                    List<String> allAnswers = new ArrayList<>(incorrectAnswers);
                    allAnswers.add(correctAnswer);
                    Collections.shuffle(allAnswers);

                    Map<String, Object> result = new HashMap<>();
                    result.put("question", question);
                    result.put("correct_answer", correctAnswer);
                    result.put("answers", allAnswers);

                    System.out.println("Sending trivia data to client: " + result);
                    return result;
                } else {
                    System.err.println("Error: Invalid response code from trivia API: " + responseCode);
                    return Collections.singletonMap("error", "Invalid trivia category or request");
                }

            } catch (WebClientResponseException.TooManyRequests e) {
                System.err.println("Received 429 Too Many Requests. Retrying in " + delay + " ms...");
                retries--;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                delay *= 2;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.singletonMap("error", "Error fetching trivia question");
            }
        }

        System.err.println("Max retries reached. Returning error response.");
        return Collections.singletonMap("error", "Max retries reached. Try again later.");
    }
}
