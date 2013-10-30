package ru.nsu.xwaf;

import java.util.Map;

/**
 *
 * @author FallDi
 */
public class ProxyFilter {

    /**
     * binding port of proxy
     */
    public static int port = 8888;
    /**
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        DatabaseManager dbm = new DatabaseManager();
        RulesGroup mainRules = dbm.loadDatabase();
        Map<Integer, String> blacklistIp = dbm.getIpBlacklist();
        FilterProxy fp = new FilterProxy(port, mainRules, blacklistIp, dbm);
        fp.start();
    }
}
