package com.abhishek.readingassessment.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFService {
    public String extractText(MultipartFile pdf) throws NullPointerException, IOException {
        PDDocument document = Loader.loadPDF(pdf.getBytes());
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String extractedText = pdfTextStripper.getText(document);
        document.close();
        return extractedText;

    }
}
