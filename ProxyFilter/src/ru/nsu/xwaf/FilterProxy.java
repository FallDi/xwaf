package ru.nsu.xwaf;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

/**
 * Proxy class. Listening all incoming connection and make thread on new client.
 *
 * @author FallDi
 */
public class FilterProxy extends Thread {

    private int port;
    private boolean listening;
    private ServerSocket serverSocket;
    private RulesGroup rules;
    private Map<Integer, String> blacklistIp;
    private DatabaseManager dbm;

    public FilterProxy(int port, RulesGroup rules, Map<Integer, String> blacklistIp, DatabaseManager dbm) {
        this.listening = true;
        this.port = port;
        this.rules = rules;
        this.blacklistIp = blacklistIp;
        this.dbm = dbm;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
            ProxyThread pt;
            while (listening) {
                pt = new ProxyThread(serverSocket.accept(), rules, blacklistIp, dbm);
                pt.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + " " + e.toString());
            System.exit(-1);
        }
    }
}
