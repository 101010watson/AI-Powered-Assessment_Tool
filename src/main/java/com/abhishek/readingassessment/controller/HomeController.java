package com.abhishek.readingassessment.controller;

import com.abhishek.readingassessment.dto.QuestionDTO;
import com.abhishek.readingassessment.dto.QuizResponseDTO;
import com.abhishek.readingassessment.service.AIService;
import com.abhishek.readingassessment.service.PDFService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class HomeController {

    private final PDFService pdfService;
    private final AIService aiService;

    public HomeController(AIService aiService, PDFService pdfService) {
        this.aiService = aiService;
        this.pdfService = pdfService;
    }


    @GetMapping("/")
    public String generator(){
         return "default-page";
     }

    @PostMapping("/generate")
    public String generate(@RequestParam(required = false) String sourceText, @RequestParam(required = false) MultipartFile pdfFile, Model model, HttpSession httpSession) throws IOException {
        // recieves the reponse object
        try{
            if (!pdfFile.isEmpty()) {
                sourceText = pdfService.extractText(pdfFile);
            }
            QuizResponseDTO quizResponseDTO = aiService.generateMcq(sourceText);
            httpSession.setAttribute("quiz", quizResponseDTO);
            model.addAttribute("quiz", quizResponseDTO);
            return "prep-page";
        } catch (Exception e) {
            model.addAttribute("error","unable to generate the quiz. Please check your internt connection");
            return "default-page";
        }
     }

     @PostMapping("/submit")
    public String submit(
            @RequestParam Map<String, String> answers,Model model,HttpSession httpSession
     ){
        QuizResponseDTO quizResponseDTO = (QuizResponseDTO) httpSession.getAttribute("quiz");
        if(quizResponseDTO == null){
            return "redirect:/quickprep";
        }
        int correct = 0;
        int incorrect = 0;
        List<QuestionDTO> question = quizResponseDTO.getQuestions();
        for(int i = 0;i < quizResponseDTO.getQuestions().size();i++){
            int selectedAnswer = Integer.parseInt(answers.get("question"+i));
            QuestionDTO currentQuestion = question.get(i);
            currentQuestion.setSelectedAnswer(selectedAnswer);
            if(selectedAnswer == currentQuestion.getCorrectAnswer()){
                correct++;
            }
            else{
                incorrect++;
            }
        }
        model.addAttribute("correct",correct);
        model.addAttribute("incorrect",incorrect);
        int scorePercentage = (correct * 100)/quizResponseDTO.getQuestions().size();
        model.addAttribute("scorePercentage",scorePercentage);
        model.addAttribute("quiz",quizResponseDTO);
        // Remove quiz from session
        httpSession.removeAttribute("quiz");
        return "report-page";
     }

}

// @RequestParam - this basically helps to get the content(in our case text) to the java spring boot variable
// HttpSession - we are using httpsession to save the context so that in the controller we can use the methods
// to get the inputs like here we want to get correctAnswers etc