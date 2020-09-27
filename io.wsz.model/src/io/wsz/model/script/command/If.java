package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.*;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableItem;
import io.wsz.model.script.bool.countable.variable.*;
import io.wsz.model.script.bool.equals.variable.BooleanStringVariableEquals;
import io.wsz.model.script.bool.equals.variable.EqualableStringVariable;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.item.HasableCreatureInventoryPlace;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;

import static io.wsz.model.script.ScriptKeyWords.*;

public class If implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseIf(Controller controller, String condition, String preBlock, ScriptValidator validator) {
        If ifCommand = new If();
        condition = condition.replaceFirst(IF, "");
        String toRemove = "";
        if (condition.startsWith(NEGATE)) {
            ifCommand.negate = true;
            toRemove = NEGATE;
        }
        condition = condition.replaceFirst(toRemove + REGEX_BRACKET_OPEN, "");
        int nextIndex = condition.lastIndexOf(BRACKET_CLOSE);

        if (nextIndex != -1) {
            validator.validateShouldNotBeEmpty(condition);
            String globalDot = GLOBAL + DOT;
            boolean negate = ifCommand.negate;

            if (condition.startsWith(globalDot)) {
                condition = condition.replaceFirst(globalDot, "");
                ifCommand.expression = parseGlobal(controller, negate, condition, validator);
            } else if (condition.startsWith(QUOTE)) {
                condition = condition.replaceFirst(QUOTE, "");
                ifCommand.expression = parseCreatureHas(controller, negate, condition, validator);
            } else {
                validator.setSyntaxInvalid(condition);
            }
            ifCommand.parsePreBlock(preBlock, ifCommand.executables, validator, controller);
            return ifCommand;
        }
        validator.setSyntaxInvalid(condition);
        return null;
    }

    private static BooleanExpression parseCreatureHas(Controller controller, boolean negate, String condition,
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
                                replaceFirst(inventoryPlaceId + QUOTE+ REGEX_BRACKET_CLOSE + REGEX_BRACKET_CLOSE, "");
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
                                        replaceFirst(amount + REGEX_BRACKET_CLOSE + REGEX_BRACKET_CLOSE, "");
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
                                CountableItem countable = new CountableItem(itemId, compareOperator, argument);
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

    private static BooleanExpression parseGlobal(Controller controller, boolean negate, String condition,
                                                 ScriptValidator validator) {
        CompareOperator compareOperator = getCompareOperator(condition);
        int nextIndex;
        if (compareOperator == null) {
            nextIndex = condition.indexOf(BRACKET_CLOSE);
            if (nextIndex != -1) {
                String globalId = condition.substring(0, nextIndex);
                validator.validateGlobalVariableHasBooleanValue(globalId);
                condition = condition.replaceFirst(globalId, "");
                condition = condition.replace(BRACKET_CLOSE, "");
                validator.validateIsEmpty(condition);
                BooleanType booleanType;
                if (negate) {
                    booleanType = BooleanType.FALSE;
                } else {
                    booleanType = BooleanType.TRUE;
                }
                return new BooleanTrueFalseGlobalVariable(globalId, booleanType);
            }
        } else {
            String operatorString = compareOperator.toString();
            nextIndex = condition.indexOf(operatorString);
            if (nextIndex != -1) {
                String globalId = condition.substring(0, nextIndex);
                condition = condition.replaceFirst(globalId + operatorString, "");
                nextIndex = condition.indexOf(BRACKET_CLOSE);

                if (nextIndex != -1) {
                    String amount = condition.substring(0, nextIndex);
                    validator.validateGlobalVariable(globalId, amount);
                    condition = condition.replaceFirst(amount, "");
                    condition = condition.replace(BRACKET_CLOSE, "");
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

    private final ArrayDeque<Executable> executables = new ArrayDeque<>();
    private boolean negate;
    private BooleanExpression expression;

    @Override
    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        expression.setChecker(controller);
        if (expression.isTrue()) { //TODO OR and AND
            for (Executable executable : executables) {
                executable.execute(controller, firstAdversary, secondAdversary);
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(negate);
        out.writeObject(expression);
        out.writeObject(executables);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        negate = in.readBoolean();
        expression = (BooleanExpression) in.readObject();
        ArrayDeque<Executable> executables = (ArrayDeque<Executable>) in.readObject();
        this.executables.addAll(executables);
    }
}