package ru.nsu.xwaf;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 *
 * @author daredevil
 */
public class RulesGroup {

    private Set<Rule> rules;
    private Rule uncheckedRule;

    public RulesGroup() {
        rules = new HashSet<Rule>();
    }

    public void addRule(Rule insertRule) {
        rules.add(insertRule);
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