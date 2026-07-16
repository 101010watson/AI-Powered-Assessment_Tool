package com.abhishek.readingassessment.controller;

import com.abhishek.readingassessment.dto.QuizResponseDTO;
import com.abhishek.readingassessment.service.AIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final AIService aiService;

    public HomeController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/quickprep")
    public String generator(){
         return "default-page";
     }

    @PostMapping("/generate")
    public String generate(@RequestParam("sourceText") String sourceText, Model model){
        // recieves the reponse object
         QuizResponseDTO quizResponseDTO = aiService.generateMcq(sourceText);
         model.addAttribute("quiz",quizResponseDTO);
         System.out.println(quizResponseDTO);
         return "prep-page";
     }
}

// @RequestParam - this basically helps to get the content(in our case text) to the java spring boot variable
