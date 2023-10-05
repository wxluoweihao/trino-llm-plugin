package com.example;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestPdfReader {
    @Test
    public void test() throws IOException {
        PdfReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String testFile = getClass().getClassLoader().getResource("example-data/car-news.pdf").getPath();
            reader = new PdfReader(testFile);
            for(int i = 1; i <= reader.getNumberOfPages(); i ++) {
                String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
                stringBuilder.append(textFromPage);
            }
            System.out.println(stringBuilder.toString());
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
