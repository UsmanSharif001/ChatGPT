package com.example.chatgptus.controller;


import com.example.chatgptus.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .headers(h -> h.setBearerAuth("NotTheRealKey"))
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


    @GetMapping("/chat1")
    public List<Choice> chatWithGPT1(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo"); // Forsk. modeller - vi vælger den gamle 3.5
        List<Message> lstMessages = new ArrayList<>();
        lstMessages.add(new Message("system", "You are a helpful assistant."));
        lstMessages.add(new Message("user", "Where is " + message));
        chatRequest.setMessages(lstMessages);
        chatRequest.setN(1); //n er antal svar der bliver givet fra ChatGPT i pp sat til 3 her til 1
        chatRequest.setTemperature(1); //jo højere jo mere fantasifuldt svar mellem 0-2 default value 1
        chatRequest.setMaxTokens(30); //længde af svar
        chatRequest.setStream(false); //false = en besked bliver sendt true = flere beskeder bliver sendt fra ChatGPT
        chatRequest.setPresencePenalty(1); //Jo lavere jo mindre gentagelse af allerede givet information -2.0 til 2.0 def value 0

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> h.setBearerAuth("NotTheRealKey"))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();
        var obj = response.getUsage();

        return lst;

    }


}