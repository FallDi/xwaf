package ru.nsu.xwaf;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 *
 * @author daredevil
 */
public class ProxyThread extends Thread {

    private Socket socket = null;
    private static final int READ_BUFFER_SIZE = 1024;
    private static final int READ_BUFFERD_SIZE_REQUEST = 1;
    private static final int BUFFER_SIZE = 32000;
    private RulesGroup rules;
    private DatabaseManager dbm;

    public ProxyThread(Socket socket, RulesGroup rules, DatabaseManager dbm) {
        super("ProxyThread");
        this.socket = socket;
        this.rules = rules;
        this.dbm = dbm;
    }

    @Override
    public void run() {
        DataOutputStream clientOut = null;
        DataInputStream clientIn = null;
        DataInputStream servIn = null;
        DataOutputStream servOut = null;
        Socket server = null;
        try {
            clientOut = new DataOutputStream(socket.getOutputStream());
            clientIn = new DataInputStream(socket.getInputStream());
            // get request
            HTTPRequest hr;
            Map<String, String> fields;
            String requestStr = null;
            int sizeRequest = 0;
            byte requestByte[] = new byte[BUFFER_SIZE];
            int index;
            while (-1 != (index = clientIn.read(requestByte, sizeRequest, READ_BUFFERD_SIZE_REQUEST))) {
                sizeRequest += index;
                requestStr = new String(requestByte).substring(0, sizeRequest);
                if (-1 != requestStr.indexOf("\r\n\r\n")) {
                    hr = new HTTPRequest(requestStr);
                    fields = hr.getFields();
                    if (null != fields.get("Content-Length")) {
                        int cl = Integer.valueOf(fields.get("Content-Length"));
                        index = clientIn.read(requestByte, sizeRequest, cl);
                        sizeRequest += index;
                        requestStr = new String(requestByte, "UTF-8").substring(0, sizeRequest);
                    }
                    break;
                }
            }
            System.out.println(requestStr);
            String[] tokens = requestStr.split(" ");
            String urlToCall = tokens[1];
            URL url = new URL(urlToCall);
            Rule blockedRule = null;
            if (null == (blockedRule = rules.isVulnerable(URLDecoder.decode(requestStr, "UTF-8")))) {
                // get response and send to client
                server = new Socket(url.getHost(), 80);
                servIn = new DataInputStream(server.getInputStream());
                servOut = new DataOutputStream(server.getOutputStream());
                servOut.write(requestByte, 0, sizeRequest);

                //begin send response to client byte by[] = new
                byte[] by = new byte[BUFFER_SIZE];
                index = servIn.read(by, 0, READ_BUFFER_SIZE);
                int responseSize = 0;
                while (index >= 0) {
                    clientOut.write(by, 0, index);
                    responseSize += index;
                    index = servIn.read(by, 0, READ_BUFFER_SIZE);
                }
            } else {
                Answer answer = new Answer("./answers/block.html");
                answer.loadFile();
                byte[] answerByte = (answer.getAnswer(requestStr, blockedRule) + blockedRule.getName()).getBytes();
                clientOut.write(answerByte, 0, answerByte.length);
                dbm.addLog(url, blockedRule, socket.getLocalAddress().getHostAddress());
                dbm.updateLogFile();
            }
            clientOut.flush();
        } catch (IOException e) {
            System.err.println(e.toString());
        } finally {
            try {
                if (null != servOut) {
                    servOut.close();
                }
                if (null != servIn) {
                    servIn.close();
                }
                if (null != server) {
                    server.close();
                }
                if (clientOut != null) {
                    clientOut.close();
                }
                if (clientIn != null) {
                    clientIn.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println(ex.toString());
            }
        }
    }
}