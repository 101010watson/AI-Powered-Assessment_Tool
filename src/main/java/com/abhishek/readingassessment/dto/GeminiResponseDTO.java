package com.abhishek.readingassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDTO {
    private List<CandidateDTO> candidates;
}
// here there is list of candidates is because Gemini's API is
// designed to send list(array) of candidates
