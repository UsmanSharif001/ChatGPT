package com.example.chatgptus.controller;


import com.example.chatgptus.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
public class ChatGPTController {

    private final WebClient webClient;

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }


    @GetMapping("/chat")
    public Map<String, Object> chatWithGPT(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        List<Message> lstMessages = new ArrayList<>();
        lstMessages.add(new Message("system", "You are a helpful assistant."));
        lstMessages.add(new Message("user", "Where is " + message));
        chatRequest.setMessages(lstMessages);
        chatRequest.setN(3);
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(30);
        chatRequest.setStream(false);
        chatRequest.setPresencePenalty(1);

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(""))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();
        Usage usg = response.getUsage();

        Map<String, Object> map = new HashMap<>();
        map.put("Usage", usg);
        map.put("Choices", lst);

        return map;


    }

    @GetMapping("/sportsTrivia")
    public Map<String, Object> getSportsTrivia() {
        String triviaApiUrl = "https://opentdb.com/api.php?amount=1&category=21&type=multiple";

        WebClient webClient = WebClient.create();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Fetch trivia data from the API
            String triviaResponse = webClient.get()
                    .uri(triviaApiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Convert JSON response to a Map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> triviaData = objectMapper.readValue(triviaResponse, Map.class);

            // Extract the trivia question and options
            List<Map<String, Object>> results = (List<Map<String, Object>>) triviaData.get("results");
            if (results != null && !results.isEmpty()) {
                Map<String, Object> trivia = results.get(0);

                String question = (String) trivia.get("question");
                List<String> incorrectAnswers = (List<String>) trivia.get("incorrect_answers");
                String correctAnswer = (String) trivia.get("correct_answer");

                // Combine correct and incorrect answers
                List<String> allAnswers = new ArrayList<>(incorrectAnswers);
                allAnswers.add(correctAnswer);
                Collections.shuffle(allAnswers);

                // Prepare response
                responseMap.put("question", question);
                responseMap.put("answers", allAnswers);
                responseMap.put("correctAnswer", correctAnswer);
            } else {
                responseMap.put("message", "No trivia found. Try again later.");
            }
        } catch (Exception e) {
            responseMap.put("error", "Failed to fetch trivia: " + e.getMessage());
        }

        return responseMap;
    }







}