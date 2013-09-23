package ru.nsu.xwaf;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author daredevil
 */
public class Answer {

    private String answer;
    private String fileName;
    private static final String REQUEST_PATTERN = "<request/>";
    private static final String BLOCKED_RULE = "<rule/>";

    public Answer(String fileName) {
        this.fileName = fileName;
        this.answer = new String();
    }

    public void loadFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));
            String strLine;
            while (null != (strLine = br.readLine())) {
                answer = answer.concat(strLine);
            }
            br.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public String getAnswer(String request, Rule rule) {
        String fullAnswer = answer.replace(REQUEST_PATTERN, request);
        fullAnswer = fullAnswer.replace(BLOCKED_RULE, rule.getName());
        return "HTTP/1.1 200 OK\r\nContent-Length: " + String.valueOf(fullAnswer.length()) + "\r\n\r\n" + fullAnswer;
    }
}