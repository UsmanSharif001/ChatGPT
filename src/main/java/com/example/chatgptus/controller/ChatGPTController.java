package com.example.chatgptus.controller;

import com.example.chatgptus.dto.ChatRequest;
import com.example.chatgptus.dto.ChatResponse;
import com.example.chatgptus.dto.Message;
import com.example.chatgptus.service.TriviaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatGPTController {

    private final TriviaService triviaService;
    private final WebClient webClient;

    @Autowired
    public ChatGPTController(TriviaService triviaService, WebClient.Builder webClientBuilder) {
        this.triviaService = triviaService;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping("/getTrivia")
    public ResponseEntity<Map<String, Object>> getTrivia(@RequestParam String category) {
        System.out.println("Received category: " + category);

        try {
            // Step 1:Her bruger vi hjælpefunktionen getTrivia til at give os spørgsmålene til endpointet fra den eksterne API
            Map<String, Object> triviaResponse = triviaService.getTrivia(category);
            if (triviaResponse.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(triviaResponse);
            }

            String question = (String) triviaResponse.get("question");
            String correctAnswer = (String) triviaResponse.get("correct_answer");

            // Step 2: Her prompter vi chatGPT til dens rolle - i vores tilfælde et quiz genererende assistent som giver os fire valgmuligheder.
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

            // // Her kalder vi på chatGPT til at give os et respons med vores key
            ChatResponse aiResponse = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth("testestes"))
                    .bodyValue(chatRequest)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            if (aiResponse == null || aiResponse.getChoices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Failed to generate AI multiple-choice options"));
            }

            // Step 3: Her henter vi den genereret besked ChatGPT har lavet
            String aiGeneratedChoices = aiResponse.getChoices().get(0).getMessage().getContent().trim();
            String[] choicesArray = aiGeneratedChoices.split("\n|,");  // Split on newline

            // Her mapper vi question til objektet "question" og choices til et string array med svarmulighederne så de kan kaldes på i frontend
            Map<String, Object> result = new HashMap<>();
            result.put("question", question);
            result.put("choices", Arrays.asList(choicesArray));

            System.out.println("AI-generated trivia data sent to client: " + result);
            return ResponseEntity.ok(result);

            // Hvis det er en error beder vi om at finde der hvor stacktracen gik galt og printe det

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal server error"));
        }
    }
}
