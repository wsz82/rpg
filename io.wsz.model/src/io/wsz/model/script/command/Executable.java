package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.ScriptValidator;

import java.util.ArrayDeque;

import static io.wsz.model.script.ScriptKeyWords.*;

public interface Executable {

    void execute(Controller controller, PosItem<?,?> firstAdversary, PosItem<?,?> secondAdversary);

    default void parsePreBlock(String s, ArrayDeque<Executable> executables,
                               ScriptValidator validator, Controller controller) {
        if (s.isEmpty()) {
            return;
        }
        int indexOfBlockOpen = s.indexOf(BLOCK_OPEN);
        if (indexOfBlockOpen != -1) {
            String preBlock = s.substring(0, indexOfBlockOpen);
            String blockOpen = s.substring(indexOfBlockOpen + 1);
            parseBlocks(preBlock, blockOpen, executables, validator, controller);
        } else {
            parseBlocks(s, "", executables, validator, controller);
        }
    }

    default void parseBlocks(String preBlock, String blockOpen, ArrayDeque<Executable> executables,
                             ScriptValidator validator, Controller controller) {

        int nextBlockClose = blockOpen.indexOf(BLOCK_CLOSE);
        nextBlockClose = getBlockCloseIndex(blockOpen, nextBlockClose);
        String nextPreBlock = "";
        String afterBlock = "";
        if (nextBlockClose != -1) {
            nextPreBlock = blockOpen.substring(0, nextBlockClose);
            afterBlock = blockOpen.substring(nextBlockClose);
            while (!afterBlock.isEmpty() && afterBlock.charAt(0) == CHAR_BLOCK_CLOSE) {
                afterBlock = afterBlock.replaceFirst(BLOCK_CLOSE, "");
            }
        }

        parseExecutables(preBlock, executables, validator, controller, nextPreBlock);
        parsePreBlock(afterBlock, executables, validator, controller);
    }

    private void parseExecutables(String preBlock, ArrayDeque<Executable> executables, ScriptValidator validator,
                                  Controller controller, String nextPreBlock) {
        String[] commandsToParse = preBlock.split(COMMAND_END);

        for (String commandToParse : commandsToParse) {
            Executable executable = null;
            if (commandToParse.startsWith(IF)) {
                executable = If.parseIf(controller, commandToParse, nextPreBlock, validator);
            } else if (commandToParse.startsWith(GLOBAL + DOT)) {
                executable = GlobalVariableSet.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(GIVE_TO_ADVERSARY)) {
                executable = GiveToAdversary.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(ADD_NEW)) {
                executable = AddNew.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(REMOVE)) {
                executable = Remove.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(RUN)) {
                executable = getScriptToRun(controller, commandToParse, validator);
            } else {
                validator.setSyntaxInvalid(commandToParse);
            }

            if (executable != null) {
                executables.add(executable);
            }
        }
    }

    private int getBlockCloseIndex(String blockOpen, int nextBlockClose) {
        if (nextBlockClose == -1) {
            return nextBlockClose;
        }
        String codeToNextBlockClose = blockOpen.substring(0, nextBlockClose);
        int openings = 0;
        int closings = 0;
        for (int i = 0; i < codeToNextBlockClose.length(); i++) {
            if (codeToNextBlockClose.charAt(i) == CHAR_BLOCK_OPEN) {
                openings++;
            } else if (codeToNextBlockClose.charAt(i) == CHAR_BLOCK_CLOSE) {
                closings++;
            }
        }

        if (openings == closings) {
            return nextBlockClose;
        } else {
            nextBlockClose = blockOpen.indexOf(BLOCK_CLOSE, nextBlockClose + 1);
            return getBlockCloseIndex(blockOpen, nextBlockClose);
        }
    }

    private Executable getScriptToRun(Controller controller, String commandToParse, ScriptValidator validator) {
        String scriptToRunId = commandToParse.replaceFirst(RUN, "");
        Executable scriptToRun = controller.getScriptById(scriptToRunId);
        if (scriptToRun == null) {
            validator.setScriptIdInvalid(scriptToRunId);
        }
        return scriptToRun;
    }
}
