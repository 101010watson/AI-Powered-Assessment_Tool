package com.abhishek.readingassessment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizResponseDTO {
    private List<QuestionDTO> questions;
}
