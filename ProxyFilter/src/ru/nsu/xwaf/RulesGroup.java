package ru.nsu.xwaf;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author FallDi
 */
public class RulesGroup {

    private Set<Rule> rules;
    private Map<Integer, String> whitelist;
    private Rule uncheckedRule;

    public RulesGroup(Map<Integer, String> whitelist) {
        this.rules = new HashSet<Rule>();
        this.whitelist = whitelist;
    }

    public void addRule(Rule insertRule) {
        rules.add(insertRule);
    }

    /**
     * 
     * @param request
     * @return contains request in whitelist or not
     */
    public boolean inWhitelist(String request) {
        HTTPRequest hr = new HTTPRequest(request);
        Set<String> items = hr.getItemType(HTTPRequest.TYPE_ITEM.REQUEST_URI_PATH);
        for (String item : items) {
            for (String urlPart : whitelist.values()) {
                Matcher m = Pattern.compile(urlPart).matcher(item);
                if (true == m.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Rule isVulnerable(String request) {
        Rule result = null;
        HTTPRequest hr = new HTTPRequest(request);
        Set<String> items;
        for (Rule rule : rules) {
            items = hr.getItemType(rule.getType());
            if (null != items) {
                for (String item : items) {
                    Matcher m = rule.getCompilePattern().matcher(item);
                    if (rule.getConditionMatch() == m.find()) {
                        System.out.println("Find vulnerable in request: " + rule.getPattern());
                        result = rule;
                        uncheckedRule = rule;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public Rule getUncheckedRule() {
        return uncheckedRule;
    }
}