package io.wsz.model.script.command;

import static io.wsz.model.script.ScriptKeyWords.*;

public interface BlockDividable extends Dividable{

    @Override
    default String getDivisionClose() {
        return BLOCK_CLOSE;
    }

    @Override
    default char getCharDivisionClose() {
        return CHAR_BLOCK_OPEN;
    }

    @Override
    default char getCharDivisionOpen() {
        return CHAR_BLOCK_CLOSE;
    }
}
