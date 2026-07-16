package com.abhishek.readingassessment.service;

import com.abhishek.readingassessment.dto.GeminiResponseDTO;
import com.abhishek.readingassessment.dto.QuizResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Service
public class AIService {
    @Value("${gemini.api.key}") // storing the key in a variable
    private String apiKey;

    // for the below two class we need to create an AppConfig
    // to implement Dependency injection

    // RestClient to communicate with the GeminiAPI
    private final RestClient restClient;

    // ObjectMapper - to convert from Json to java objects or vice versa
    private final ObjectMapper objectMapper;

    //  Here we are using a builder instead of create method is because later we want to customize our request using builder
    public AIService(RestClient.Builder builder, ObjectMapper objectMapper) {
        this.restClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public QuizResponseDTO generateMcq(String sourceText){
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key="+apiKey;
        String prompt = """
        You are an expert reading comprehension examiner.
        
        Generate exactly 10 multiple-choice questions based ONLY on the passage below.
        
        Requirements:
        - Return ONLY valid JSON.
        - Do not wrap the response in ```json ... ```.
        - Do not include any explanation or introductory text.
        - Return exactly one JSON object matching the schema below.
        - Do not include markdown or explanations.
        - Each question must have:
          - question
          - options (4 options)
          - correctAnswer (0-3 index)
          - category (Factual, Inference, Vocabulary)
        
        JSON Format:
        {
          "questions": [
            {
              "question": "...",
              "options": ["...", "...", "...", "..."],
              "correctAnswer": 0,
              "category": "Factual"
            }
          ]
        }
        
        Passage:
        %s
        """.formatted(sourceText);
        // we create %s and then format it because it is cleaner to do so

        String requestBody; // request body or contract format to communicate with GeminiAPI
        try{
            requestBody = """
                    {
                        "contents":[
                            {
                                "parts":[
                                    {
                                        "text": %s
                                    }
                                ]
                            }
                        ]
                    }
                    """.formatted(objectMapper.writeValueAsString(prompt)); // we used objectMapper.method to convert it into a valid json
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }// try-catch is used because it can throw a jsonexception sometimes

        // here we will be STORING the response of gemini
        // for the request that we are making through restClient
        // below is the syntax to communicate with an API
        GeminiResponseDTO response = restClient
                .post() // Send a POST request
                .uri(url)// To this URL
                .contentType(MediaType.APPLICATION_JSON) // to tell the api that the request is being transferred in JSON format
                .body(requestBody) // Send this JSON
                .retrieve()// Get the response
                .body(GeminiResponseDTO.class);// Convert JSON → GeminiResponseDTO Object

        //before we extract we have to check if the response is null
        if(response == null){
            throw new RuntimeException("NO response recieved from Gemini.");
        }
        if(response.getCandidates() == null || response.getCandidates().isEmpty()){
            throw new RuntimeException("Gemini returned no candiates.");
        }
        if(response.getCandidates().getFirst().getContent() == null){
            throw new RuntimeException("Gemini return no content.");
        }
        if(response.getCandidates().getFirst().getContent().getParts() == null
        || response.getCandidates().getFirst().getContent().getParts().isEmpty()){
            throw new RuntimeException("Gemini returned no parts");
        }

        // here we store all the content from the response we get
        // and that is possible through geminiresponseDTO
        String json = response
                .getCandidates()
                .getFirst()
                .getContent()
                .getParts()
                .getFirst()
                .getText();

        // converts the json string into QuizResponseDTO object.
        QuizResponseDTO quizResponseDTO = objectMapper.readValue(json, QuizResponseDTO.class);
        return quizResponseDTO;

    }
}
// A RestClient is HTTP Client that helps to consume API
// or in simple terms RestClient helps to use the API into your application

// --> DTO - Data Transfer Object :
// A DTO (Data Transfer Object) is simply an object whose only job is to transfer data
// between two parts of your application (or between your app and another service).