package ru.nsu.xwaf;

import java.util.regex.Pattern;
import ru.nsu.xwaf.HTTPRequest.TYPE_ITEM;

/**
 *
 * @author daredevil
 */
public class Rule {

    public enum RULE_ACTION {

        BLOCK
    };

    public enum CONDITION {

        MATCH,
        NO_MATCH
    }
    private int id;
    private String name;
    private HTTPRequest.TYPE_ITEM type;
    private String pattern;
    private boolean caseSensitive;
    private RULE_ACTION action;
    private CONDITION condition;
    private int weight;

    public Rule(int id, String name, TYPE_ITEM type, String pattern, boolean caseSensitive, RULE_ACTION action, CONDITION condition, int weight) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pattern = pattern;
        this.caseSensitive = caseSensitive;
        this.action = action;
        this.condition = condition;
        this.weight = weight;
    }

    /**
     * Get the value of condition
     *
     * @return the value of condition
     */
    public CONDITION getCondition() {
        return condition;
    }

    /**
     * Get the value of weight
     *
     * @return the value of weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Set the value of condition
     *
     * @param condition new value of condition
     */
    public void setCondition(CONDITION condition) {
        this.condition = condition;
    }

    public RULE_ACTION getAction() {
        return action;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public TYPE_ITEM getType() {
        return type;
    }

    public boolean getConditionMatch() {
        if (condition == CONDITION.MATCH) {
            return true;
        } else if (condition == CONDITION.NO_MATCH) {
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(id) + " " + name + " " + type.toString() + " " + pattern + " " + caseSensitive + " " + action.toString() + " " + condition.toString() + " " + String.valueOf(weight);
    }

    public Pattern getCompilePattern() {
        String p = pattern;
        if (caseSensitive) {
            p = p.concat("(?i)");
        }
        Pattern result = Pattern.compile(p);
        return result;
    }
}
