package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.ScriptValidator;

import java.util.ArrayDeque;

import static io.wsz.model.script.ScriptKeyWords.*;

public interface Executable {

    boolean tryExecute(Controller controller, PosItem<?,?> firstAdversary, PosItem<?,?> secondAdversary);

    default void parsePreBlock(String all, ArrayDeque<Executable> executables,
                               ScriptValidator validator, Controller controller) {
        if (all.isEmpty()) {
            return;
        }
        int indexOfBlockOpen = all.indexOf(BLOCK_OPEN);
        if (all.startsWith(ELSE)) {
            int length = all.length();
            try {
                all = all.substring(indexOfBlockOpen + 1, length - 1);
            } catch (Exception e) {
                validator.setSyntaxInvalid(all);
                e.printStackTrace();
            }
            indexOfBlockOpen = all.indexOf(BLOCK_OPEN);
        }
        String preBlock = "";
        String nextPreBlock = "";
        String tempAfterBlock = "";

        if (indexOfBlockOpen != -1) {
            preBlock = all.substring(0, indexOfBlockOpen);
            String blockOpen = all.substring(indexOfBlockOpen);

            int nextBlockClose = getBlockCloseIndex(blockOpen);
            if (nextBlockClose != -1) {
                nextPreBlock = removeBlockOpenings(blockOpen.substring(1, nextBlockClose));
                tempAfterBlock = removeBlockOpenings(blockOpen.substring(nextBlockClose));
            }
        } else {
            preBlock = all;
        }
        String[] afterBlock = new String[]{tempAfterBlock};
        parseExecutables(preBlock, nextPreBlock, afterBlock, executables, validator, controller);
        parsePreBlock(afterBlock[0], executables, validator, controller);
    }

    private String removeBlockOpenings(String substring) {
        String tempAfterBlock;
        tempAfterBlock = substring;
        while (!tempAfterBlock.isEmpty() && tempAfterBlock.charAt(0) == CHAR_BLOCK_CLOSE) {
            tempAfterBlock = tempAfterBlock.replaceFirst(BLOCK_CLOSE, "");
        }
        return tempAfterBlock;
    }

    private void parseExecutables(String preBlock, String nextPreBlock, String[] afterBlock,
                                  ArrayDeque<Executable> executables, ScriptValidator validator, Controller controller) {
        String[] commandsToParse = preBlock.split(COMMAND_END);

        for (String commandToParse : commandsToParse) {
            Executable executable = null;
            if (commandToParse.startsWith(IF)) {
                executable = If.parseIf(controller, commandToParse, nextPreBlock, afterBlock, validator);
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

    default int getBlockCloseIndex(String blockOpen) {
        return getBlockCloseIndex(blockOpen, blockOpen.indexOf(BLOCK_CLOSE));
    }

    private int getBlockCloseIndex(String blockOpen, int nextBlockClose) {
        if (nextBlockClose == -1) {
            return nextBlockClose;
        }
        String codeToNextBlockClose = blockOpen.substring(0, nextBlockClose + 1);
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
