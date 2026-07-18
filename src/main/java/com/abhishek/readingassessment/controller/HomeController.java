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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

     // (required = false) means that if user uploads pdf and leaves text box empty
     // "sourceText is optional. If it's not sent, don't throw an error."
     // and Similarly for the pdf File too
    @PostMapping("/generate")
    public String generate(@RequestParam(required = false) String sourceText,
                           @RequestParam(required = false) MultipartFile pdfFile,
                           Model model, HttpSession httpSession,
                           RedirectAttributes redirectAttributes){

        boolean hasPdf = pdfFile != null && !pdfFile.isEmpty();
        boolean hasText = sourceText != null && !sourceText.trim().isEmpty();

        // if user does not paste or upload any content
        if(!hasPdf && !hasText){
            redirectAttributes.addFlashAttribute(
                    "error","Please paste some text or upload a PDF."
            );
            return "redirect:/";
        }

        try{
            if (hasPdf) {
                sourceText = pdfService.extractText(pdfFile);
            }
            // receives the response object
            QuizResponseDTO quizResponseDTO = aiService.generateMcq(sourceText);
            httpSession.setAttribute("quiz", quizResponseDTO); // stores the response in the session for later use
            model.addAttribute("quiz", quizResponseDTO);
            return "prep-page";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to generate the quiz. Please check your internet connection"
            );
            return "redirect:/";
        }
     }

    @PostMapping("/submit")
    public String submit(
            @RequestParam Map<String, String> answers,Model model,HttpSession httpSession
     ){
        QuizResponseDTO quizResponseDTO = (QuizResponseDTO) httpSession.getAttribute("quiz");
        if(quizResponseDTO == null){
            return "redirect:/";
        }
        int correct = 0;
        int incorrect = 0;
        List<QuestionDTO> question = quizResponseDTO.getQuestions(); // retrieve the questions in form of List
        for(int i = 0;i < quizResponseDTO.getQuestions().size();i++){
            int selectedAnswer = Integer.parseInt(answers.get("question"+i)); // answer by user
            QuestionDTO currentQuestion = question.get(i);
            currentQuestion.setSelectedAnswer(selectedAnswer); // store the selected answer for later comparision
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

// HttpSession - we are using HttpSession to save the context so that in the controller we can use the methods
// to get the inputs like here we want to get correctAnswers etc

// --> addFlashAttribute()
// Flash attributes are specifically designed for this situation(confirm resubmission after an error is displayed).
// Spring temporarily stores the attribute (internally using the session),
// then automatically copies it into the model of the next request only.