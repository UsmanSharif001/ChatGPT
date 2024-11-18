package com.example.chatgptus.repository;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TriviaRepository {

    private final WebClient webClient;

    public TriviaRepository(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://opentdb.com/api.php").build();
    }

    public Map<String, Object> getTriviaQuestion(String category) {

        // Vores eksterne api som vi bruger til at give os den information der skal bruges til quizzen
        String triviaApiUrl = "https://opentdb.com/api.php?amount=1&type=multiple&category=" + category;
        System.out.println("Calling Trivia API: " + triviaApiUrl);

        // I den her venlige version har du unlimited amounts of guesses!
        // Laver en webClient som kalder på vores api og omdanner det til et json-objekt som vi så kan bruge i vores getTrivia metode
        while (true) {
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
                return Collections.singletonMap("error", "Please wait a couple of seconds before next question");
            }
        }
    }









}
