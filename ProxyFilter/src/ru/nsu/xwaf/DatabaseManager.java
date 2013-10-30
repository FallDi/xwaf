package ru.nsu.xwaf;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Database management class
 *
 * @author FallDi
 */
public class DatabaseManager {

    public static String DB_PATH = "../vulnerabilities_signatures.sqlite";
    public static String LOG_FILE_NAME = "./logs/result.html";
    public static String RULE_TABLE_NAME = "rules";
    public static String LOGGER_TABLE_NAME = "logger";
    public static String BLACKLIST_IP_TABLE_NAME = "blacklistIp";
    public static String WHITELIST_TABLE_NAME = "whitelist";

    public DatabaseManager() {
    }

    /**
     * Connect to database and get all rules
     *
     * @return Set of rules from database
     */
    public RulesGroup loadDatabase() {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        RulesGroup rulesGroup = new RulesGroup(this.getWhitelist());
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT id, name, type, pattern, case_sensitive, action, condition, weight FROM " + RULE_TABLE_NAME);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int weight = resultSet.getInt("weight");
                String name = resultSet.getString("name");
                String typeStr = resultSet.getString("type");
                HTTPRequest.TYPE_ITEM type = HTTPRequest.TYPE_ITEM.PARAM_VALUE;
                if (0 == typeStr.compareTo("requestUriPath")) {
                    type = HTTPRequest.TYPE_ITEM.REQUEST_URI_PATH;
                } else if (0 == typeStr.compareTo("paramValue")) {
                    type = HTTPRequest.TYPE_ITEM.PARAM_VALUE;
                } else if (0 == typeStr.compareTo("requestLine")) {
                    type = HTTPRequest.TYPE_ITEM.REQUEST_LINE;
                }
                String pattern = resultSet.getString("pattern");
                String caseSensitiveStr = resultSet.getString("case_sensitive");
                boolean caseSensitive = true;
                if (0 == caseSensitiveStr.compareTo("no")) {
                    caseSensitive = false;
                }
                String actionStr = resultSet.getString("action");
                Rule.RULE_ACTION action = Rule.RULE_ACTION.BLOCK;
                if (0 == actionStr.compareTo("block")) {
                    action = Rule.RULE_ACTION.BLOCK;
                }
                String conditionStr = resultSet.getString("condition");
                Rule.CONDITION condition = Rule.CONDITION.MATCH;
                if (0 == conditionStr.compareTo("match")) {
                    condition = Rule.CONDITION.MATCH;
                } else if (0 == conditionStr.compareTo("no match")) {
                    condition = Rule.CONDITION.NO_MATCH;
                }
                Rule rule = new Rule(id, name, type, pattern, caseSensitive, action, condition, weight);
                rulesGroup.addRule(rule);
                System.out.println(rule.toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return rulesGroup;
    }

    public Map<Integer, String> getIpBlacklist() {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        HashMap<Integer, String> blacklistIp = new HashMap<Integer, String>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT id, ip FROM " + BLACKLIST_IP_TABLE_NAME);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ip = resultSet.getString("ip");
                blacklistIp.put(id, ip);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return blacklistIp;
    }

    public Map<Integer, String> getWhitelist() {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        HashMap<Integer, String> whitelist = new HashMap<Integer, String>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT id, urlPart FROM " + WHITELIST_TABLE_NAME);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ip = resultSet.getString("urlPart");
                whitelist.put(id, ip);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
        return whitelist;
    }

    public String getLog(GregorianCalendar from, GregorianCalendar to) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String result = new String();
        result = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n"
                + "    <head>\n"
                + "        <title>Logger</title>\n"
                + "        <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n"
                + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />\n"
                + "    </head>\n"
                + "    <body>\n";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.prepareStatement("SELECT rule_id, COUNT(*) FROM " + LOGGER_TABLE_NAME + " WHERE datetime >= ? AND datetime <= ? GROUP BY rule_id");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            statement.setString(1, dateFormat.format(from.getTime()));
            statement.setString(2, dateFormat.format(to.getTime()));
            resultSet = statement.executeQuery();
            result += "<p>Log from <b>" + dateFormat.format(from.getTime()) + "</b> to <b>" + dateFormat.format(to.getTime()) + "</b></p>\n";
            result += "<table border=\"1\">\n";
            result += "<tr>\n\t<td align=\"center\" class=\"table_title\">Rule</td>\n\t<td align=\"center\" class=\"table_title\">Activation</td>\n</tr>\n";
            while (resultSet.next()) {
                statement = connection.prepareStatement("SELECT name FROM " + RULE_TABLE_NAME + " WHERE id = ?");
                statement.setString(1, resultSet.getString("rule_id"));
                ResultSet ruleSet = statement.executeQuery();
                String rule_id = ruleSet.getString("name");
                String count = resultSet.getString("COUNT(*)");
                result += "<tr>\n\t<td>" + rule_id + "</td>\n\t<td>" + count + "</td>\n</tr>\n";
            }
            result += "</table>";
            statement.close();
            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        result += "     </body>\n"
                + "</html>";
        return result;
    }

    public void addLog(URL url, Rule rule, String sourceIP) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.prepareStatement("INSERT INTO " + LOGGER_TABLE_NAME + " VALUES(NULL, ?, ?, ?, ?)");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            statement.setString(1, dateFormat.format(cal.getTime()));
            statement.setString(2, url.toString());
            statement.setInt(3, rule.getId());
            statement.setString(4, sourceIP);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void updateLogFile() {
        try {
            GregorianCalendar start = new GregorianCalendar(2012, 05 - 1, 17);
            GregorianCalendar end = new GregorianCalendar();
            String log = this.getLog(start, end);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(new FileOutputStream(LOG_FILE_NAME))));
            bw.write(log, 0, log.length());
            bw.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
