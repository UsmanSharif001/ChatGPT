package com.example.chatgptus.service;

import com.example.chatgptus.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TriviaService {

    private final TriviaRepository triviaRepository;

    @Autowired
    public TriviaService(TriviaRepository triviaRepository) {
        this.triviaRepository = triviaRepository;
    }

    public Map<String, Object> getTrivia(String category) {
        return triviaRepository.getTriviaQuestion(category);
    }


}
