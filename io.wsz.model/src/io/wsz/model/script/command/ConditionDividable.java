package io.wsz.model.script.command;

import static io.wsz.model.script.ScriptKeyWords.*;

public interface ConditionDividable extends Dividable {

    @Override
    default String getDivisionClose() {
        return BRACKET_CLOSE;
    }

    @Override
    default char getCharDivisionClose() {
        return CHAR_BRACKET_CLOSE;
    }

    @Override
    default char getCharDivisionOpen() {
        return CHAR_BRACKET_OPEN;
    }
}
