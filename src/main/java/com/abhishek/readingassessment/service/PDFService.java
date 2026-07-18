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
//        The try (...) syntax is called try-with-resources.
//        It automatically calls document.close() even if an exception occurs while reading the PDF
        try(PDDocument document = Loader.loadPDF(pdf.getBytes())){
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(document);
        }

    }
}

/*
 * PDFBox is a Java library used to read and work with PDF files.
 *
 * Flow:
 * User uploads PDF
 *        ↓
 * MultipartFile (Spring stores the uploaded file)
 *        ↓
 * getBytes() → Converts the uploaded PDF into raw byte[]
 *        ↓
 * Loader.loadPDF(byte[]) → Opens the PDF and creates a PDDocument object
 *        ↓
 * PDFTextStripper → Reads the opened PDF and extracts all readable text
 *        ↓
 * String extractedText → Returned to the controller and sent to the AI
 *
 * Note:
 * - PDDocument represents an opened PDF in memory.
 * - Always close the PDDocument after use to free resources.
 * - throws IOException is used because reading files can fail
 *   (e.g., corrupted PDF, I/O error, upload issue).
 */
