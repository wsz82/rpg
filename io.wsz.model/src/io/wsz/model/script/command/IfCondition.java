package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.Not;
import io.wsz.model.script.ScriptValidator;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableItem;
import io.wsz.model.script.bool.countable.variable.BooleanDecimalGlobalVariable;
import io.wsz.model.script.bool.countable.variable.BooleanIntegerGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableDecimalVariable;
import io.wsz.model.script.bool.countable.variable.CountableIntegerVariable;
import io.wsz.model.script.bool.equals.variable.BooleanStringVariableEquals;
import io.wsz.model.script.bool.equals.variable.BooleanTrueFalseGlobalVariable;
import io.wsz.model.script.bool.equals.variable.EqualableStringVariable;
import io.wsz.model.script.bool.equals.variable.EqualableTrueFalse;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.item.HasableCreatureInventoryPlace;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.script.ScriptKeyWords.*;

public class IfCondition implements ConditionDividable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static IfCondition parseIfCondition(String anIf, String condition, Controller controller, ScriptValidator validator) {
        IfCondition ifCondition = new IfCondition();

        condition = condition.replaceFirst(anIf, "");
        if (condition.startsWith(NEGATE)) {
            ifCondition.negate = true;
            condition = condition.substring(1);
        }
        String andOrIfCondition = "";
        if (condition.startsWith(BRACKET_OPEN)) {
            int indexOfClosingBracket = ifCondition.getDivisionCloseIndex(condition);
            if (indexOfClosingBracket != -1) {
                String innerCondition = condition.substring(1, indexOfClosingBracket);
                ifCondition.innerCondition = IfCondition.parseIfCondition("", innerCondition, controller, validator);
                andOrIfCondition = condition.substring(indexOfClosingBracket + 1);
            }
        } else {
            int andIndex = condition.indexOf(AND);
            int orIndex = condition.indexOf(OR);
            int beginOfFollowingIndex = Math.min(andIndex, orIndex);
            if (andIndex == -1) beginOfFollowingIndex = orIndex;
            if (orIndex == -1) beginOfFollowingIndex = andIndex;
            int indexOfNextCondition = condition.indexOf(BRACKET_OPEN);
            boolean conditionContainsOperator = beginOfFollowingIndex != -1 &&
                    (indexOfNextCondition == -1 || beginOfFollowingIndex < indexOfNextCondition);
            if (conditionContainsOperator) {
                andOrIfCondition = condition.substring(beginOfFollowingIndex);
                condition = condition.substring(0, beginOfFollowingIndex);
            }
            ifCondition.expression = parseExpression(condition, ifCondition, controller, validator);
        }
        setUpAndOrIfCondition(ifCondition, andOrIfCondition, controller, validator);
        return ifCondition;
    }

    private static void setUpAndOrIfCondition(IfCondition ifCondition, String followingCondition,
                                              Controller controller, ScriptValidator validator) {
        if (followingCondition.isEmpty()) return;
        String operatorToRemove = "";
        BooleanOperator operator = null;
        if (followingCondition.startsWith(AND)) {
            operator = BooleanOperator.AND;
            operatorToRemove = AND;
        } else if (followingCondition.startsWith(OR)) {
            operator = BooleanOperator.OR;
            operatorToRemove = OR;
        }
        if (operator != null) {
            ifCondition.operator = operator;
            followingCondition = followingCondition.replaceFirst(operatorToRemove, "");
            ifCondition.andOrIfCondition = IfCondition.parseIfCondition("", followingCondition, controller, validator);
        }
    }

    private static BooleanExpression<?> parseExpression(String condition, IfCondition ifCondition,
                                                        Controller controller, ScriptValidator validator) {
        validator.validateShouldNotBeEmpty(condition);
        String globalDot = GLOBAL + DOT;
        boolean negate = ifCondition.isNegate();

        BooleanExpression<?> expression = null;
        if (condition.startsWith(globalDot)) {
            condition = condition.replaceFirst(globalDot, "");
            expression = parseGlobal(controller, negate, condition, validator);
        } else if (condition.startsWith(QUOTE)) {
            condition = condition.replaceFirst(QUOTE, "");
            //TODO containerHas
            expression = parseCreatureHas(controller, negate, condition, validator);
        } else {
            validator.setSyntaxInvalid(condition);
        }
        return expression;
    }

    private static BooleanExpression<?> parseCreatureHas(Controller controller, boolean negate, String condition,
                                                         ScriptValidator validator) {
        int nextIndex = condition.indexOf(QUOTE);
        if (nextIndex != -1) {
            String creatureId = condition.substring(0, nextIndex);
            validator.validateCreature(creatureId);
            condition = condition.replaceFirst(creatureId + QUOTE + DOT + HAS, "");

            CompareOperator compareOperator = getCompareOperator(condition);

            if (compareOperator == null) {
                if (condition.startsWith(INVENTORY_PLACE)) {
                    condition = condition.replaceFirst(INVENTORY_PLACE + REGEX_BRACKET_OPEN + QUOTE, "");
                    nextIndex = condition.indexOf(QUOTE);
                    if (nextIndex != -1) {
                        String inventoryPlaceId = condition.substring(0, nextIndex);
                        validator.validateInventoryPlaceId(inventoryPlaceId);
                        condition = condition.
                                replaceFirst(inventoryPlaceId + QUOTE + REGEX_BRACKET_CLOSE, "");
                        validator.validateIsEmpty(condition);
                        Not not = null;
                        if (negate) {
                            not = Not.NOT;
                        }
                        HasableCreatureInventoryPlace hasable = new HasableCreatureInventoryPlace(inventoryPlaceId, not);
                        return new BooleanCreatureHasInventoryPlace(creatureId, hasable);
                    }
                }
            } else {
                String operatorString = compareOperator.toString();
                if (condition.startsWith(ITEM)) {
                    condition = condition.replaceFirst(ITEM + REGEX_BRACKET_OPEN + QUOTE, "");
                    nextIndex = condition.indexOf(QUOTE);
                    if (nextIndex != -1) {
                        String itemId = condition.substring(0, nextIndex);
                        validator.validateItemOrAsset(itemId);
                        condition = condition.replaceFirst(itemId + QUOTE, "");

                        nextIndex = condition.indexOf(operatorString);

                        if (nextIndex != -1) {
                            condition = condition.replaceFirst(operatorString, "");
                            nextIndex = condition.indexOf(BRACKET_CLOSE);

                            if (nextIndex != -1) {
                                String amount = condition.substring(0, nextIndex);
                                validator.validateInteger(amount);
                                condition = condition.
                                        replaceFirst(amount + REGEX_BRACKET_CLOSE, "");
                                validator.validateIsEmpty(condition);

                                if (negate) {
                                    compareOperator = compareOperator.getNegate();
                                }

                                int argument = 0;
                                try {
                                    argument = Integer.parseInt(amount);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                //todo comapirng to global
                                CountableItem countable = new CountableItem(itemId, compareOperator, argument, null);
                                return new BooleanItemVsItem(creatureId, countable);
                            }
                        }
                    }
                }
            }
        }
        validator.setSyntaxInvalid(condition);
        return null;
    }

    private static BooleanExpression<?> parseGlobal(Controller controller, boolean negate, String condition,
                                                    ScriptValidator validator) {
        CompareOperator compareOperator = getCompareOperator(condition);
        int nextIndex;
        if (compareOperator == null) {
            String globalId = condition;
            validator.validateGlobalVariableHasBooleanValue(globalId);
            condition = condition.replaceFirst(globalId, "");
            validator.validateIsEmpty(condition);
            EqualsOperator equalsOperator;
            if (negate) {
                equalsOperator = EqualsOperator.EQUAL;
            } else {
                equalsOperator = EqualsOperator.NOT_EQUAL;
            }
            EqualableTrueFalse equalable = new EqualableTrueFalse(null, equalsOperator, true);
            return new BooleanTrueFalseGlobalVariable(globalId, equalable);
        } else {
            String operatorString = compareOperator.toString();
            nextIndex = condition.indexOf(operatorString);
            if (nextIndex != -1) {
                String globalId = condition.substring(0, nextIndex);
                condition = condition.replaceFirst(globalId + operatorString, "");

                String amount = condition;
                validator.validateGlobalVariable(globalId, amount);
                condition = condition.replaceFirst(amount, "");
                validator.validateIsEmpty(condition);

                Variable<?> variable = controller.getGlobalVariableById(globalId);
                if (variable != null) {
                    Object value = variable.getValue();
                    if (value instanceof Integer) {
                        //todo comapirng to global
                        CountableIntegerVariable countable = new CountableIntegerVariable(null, compareOperator, (int) value);
                        return new BooleanIntegerGlobalVariable(globalId, countable);
                    } else if (value instanceof Double) {
                        //todo comapirng to global
                        CountableDecimalVariable countable = new CountableDecimalVariable(null, compareOperator, (double) value);
                        return new BooleanDecimalGlobalVariable(globalId, countable);
                    } else if (value instanceof String) {
                        String compareString = compareOperator.toString();
                        EqualsOperator equalsOperator = getEqualsOperator(compareString);
                        validator.validateEqualsOperator(equalsOperator, compareString);
                        switch (equalsOperator) {
                            case EQUAL -> {
                                if (negate) {
                                    equalsOperator = EqualsOperator.NOT_EQUAL;
                                }
                            }
                            case NOT_EQUAL -> {
                                if (negate) {
                                    equalsOperator = EqualsOperator.EQUAL;
                                }
                            }
                        }
                        //todo comapirng to global
                        EqualableStringVariable equalable = new EqualableStringVariable(null, equalsOperator, amount);
                        return new BooleanStringVariableEquals(globalId, equalable);
                    }
                }
            }
        }
        validator.setSyntaxInvalid(condition);
        return null;
    }

    private static EqualsOperator getEqualsOperator(String compareString) {
        EqualsOperator equalsOperator = null;
        for (EqualsOperator operator : EqualsOperator.values()) {
            if (compareString.equals(operator.toString())) {
                equalsOperator = operator;
            }
        }
        return equalsOperator;
    }

    private static CompareOperator getCompareOperator(String condition) {
        CompareOperator compareOperator = null;
        for (CompareOperator operator : CompareOperator.values()) {
            String sign = operator.toString();
            boolean contains = condition.contains(sign);
            if (contains) {
                compareOperator = operator;
                break;
            }
        }
        return compareOperator;
    }

    private boolean negate;
    private BooleanExpression<?> expression;
    private IfCondition innerCondition;
    private BooleanOperator operator;
    private IfCondition andOrIfCondition;

    public boolean isTrue(Controller controller) {
        boolean isTrue = false;
        if (expression != null) {
            expression.setUpVariables(controller, null);
            isTrue = expression.isTrue();
        } else if (innerCondition != null) {
            isTrue = innerCondition.isTrue(controller);
        }
        if (operator != null && andOrIfCondition != null) {
            switch (operator) {
                case AND -> isTrue = isTrue && andOrIfCondition.isTrue(controller);
                case OR -> isTrue = isTrue || andOrIfCondition.isTrue(controller);
            }
        }
        if (negate) {
            return !isTrue;
        } else {
            return isTrue;
        }
    }

    public boolean isNegate() {
        return negate;
    }

    public BooleanExpression<?> getExpression() {
        return expression;
    }

    public IfCondition getInnerCondition() {
        return innerCondition;
    }

    public BooleanOperator getOperator() {
        return operator;
    }

    public IfCondition getAndOrIfCondition() {
        return andOrIfCondition;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(negate);
        out.writeObject(expression);
        out.writeObject(innerCondition);
        out.writeObject(operator);
        out.writeObject(andOrIfCondition);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        negate = in.readBoolean();
        expression = (BooleanExpression<?>) in.readObject();
        innerCondition = (IfCondition) in.readObject();
        operator = (BooleanOperator) in.readObject();
        andOrIfCondition = (IfCondition) in.readObject();
    }
}
