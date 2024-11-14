package com.example.chatgptus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TriviaService {
    private final WebClient webClient;

    public TriviaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://opentdb.com").build();
    }

    public String getTriviaQuestion() {
        String triviaApiUrl = "/api.php?amount=1&category=21&type=multiple";
        TriviaQuestion triviaQuestion = webClient.get()
                .uri(triviaApiUrl)
                .retrieve()
                .bodyToMono(TriviaQuestion.class)
                .block();

        if (triviaQuestion != null && !triviaQuestion.getResults().isEmpty()) {
            TriviaQuestion.Result result = triviaQuestion.getResults().get(0);
            return result.getQuestion() + "\nOptions: " + result.getIncorrect_answers() + "\nCorrect Answer: " + result.getCorrect_answer();
        }
        return "No trivia question available";
    }
}
