package ru.nsu.xwaf;

/**
 *
 * @author daredevil
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
        FilterProxy fp = new FilterProxy(port, mainRules, dbm);
        fp.start();
    }
}
