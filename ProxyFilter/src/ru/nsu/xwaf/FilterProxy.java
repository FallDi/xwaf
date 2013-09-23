package ru.nsu.xwaf;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author daredevil
 */
public class FilterProxy extends Thread {

    private int port;
    private boolean listening;
    private ServerSocket serverSocket;
    private RulesGroup rules;
    private DatabaseManager dbm;
    
    public FilterProxy(int port, RulesGroup rules, DatabaseManager dbm) {
        listening = true;
        this.port = port;
        this.rules = rules;
        this.dbm = dbm;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
            ProxyThread pt;
            while (listening) {
                pt = new ProxyThread(serverSocket.accept(), rules, dbm);
                pt.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
    }
}
