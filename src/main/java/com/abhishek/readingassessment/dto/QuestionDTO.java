package com.abhishek.readingassessment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionDTO {
    private String question;
    private List<String> options;
    private int correctAnswer;
    private String category;
    private Integer selectedAnswer;
}
