package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.ScriptValidator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;

import static io.wsz.model.script.ScriptKeyWords.*;

public class If implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseIf(Controller controller, String condition, String preBlock, String[] afterBlock, ScriptValidator validator) {
        If ifCommand = new If();

        IfExecutable ifExecutable = new IfExecutable();
        ArrayDeque<IfExecutable> ifExecutables = ifCommand.getIfExecutables();
        ifExecutables.add(ifExecutable);

        ifExecutable.setCondition(IfCondition.parseIfCondition(IF, condition, controller, validator));
        ifExecutable.parsePreBlock(preBlock, ifExecutable.getExecutables(), validator, controller);

        while (afterBlock[0].startsWith(ELSE)) {
            String prev = afterBlock[0];
            parseNextElse(controller, afterBlock, validator, ifCommand, ifExecutables);
            if (prev.equals(afterBlock[0])) {
                validator.setSyntaxInvalid(prev);
                break;
            }
        }

        return ifCommand;
    }

    protected static void parseNextElse(Controller controller, String[] afterBlock, ScriptValidator validator, If ifCommand, ArrayDeque<IfExecutable> ifExecutables) {
        String tempAfterBlock = afterBlock[0];
        if (tempAfterBlock.startsWith(ELSE)) {
            int blockOpenIndex = tempAfterBlock.indexOf(BLOCK_OPEN);
            String nextIfCondition = "";
            if (blockOpenIndex != -1) {
                nextIfCondition = tempAfterBlock.substring(0, blockOpenIndex);
            }
            validator.validateShouldNotBeEmpty(nextIfCondition);

            int nextBlockClose = ifCommand.getDivisionCloseIndex(tempAfterBlock);
            String elseBlock = "";
            if (nextBlockClose != -1) {
                elseBlock = tempAfterBlock.substring(0, nextBlockClose + 1);
            }

            if (tempAfterBlock.startsWith(ELSE_IF)) {
                IfExecutable elseIfExecutable = new IfExecutable();
                elseIfExecutable.setCondition(IfCondition.parseIfCondition(ELSE_IF, nextIfCondition, controller, validator));

                elseIfExecutable.parsePreBlock(elseBlock, elseIfExecutable.getExecutables(), validator, controller);
                ifExecutables.add(elseIfExecutable);
            } else {
                ifCommand.parsePreBlock(elseBlock, ifCommand.elseExecutables, validator, controller);
            }
            afterBlock[0] = afterBlock[0].replace(elseBlock, "");
        }
    }

    private final ArrayDeque<IfExecutable> ifExecutables = new ArrayDeque<>();
    private final ArrayDeque<Executable> elseExecutables = new ArrayDeque<>();

    @Override
    public boolean tryExecute(Controller controller, PosItem<?, ?> firstAdversary, PosItem<?, ?> secondAdversary) {
        boolean isExecuted = false;
        for (IfExecutable ifExecutable : ifExecutables) {
            isExecuted = ifExecutable.tryExecute(controller, firstAdversary, secondAdversary);
            if (isExecuted) break;
        }
        if (!isExecuted) {
            for (Executable executable : elseExecutables) {
                executable.tryExecute(controller, firstAdversary, secondAdversary);
            }
        }
        return true;
    }

    public ArrayDeque<IfExecutable> getIfExecutables() {
        return ifExecutables;
    }

    public ArrayDeque<Executable> getElseExecutables() {
        return elseExecutables;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(ifExecutables);
        out.writeObject(elseExecutables);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ArrayDeque<IfExecutable> inIfExecutables = (ArrayDeque<IfExecutable>) in.readObject();
        ifExecutables.addAll(inIfExecutables);

        ArrayDeque<Executable> inElseExecutables = (ArrayDeque<Executable>) in.readObject();
        elseExecutables.addAll(inElseExecutables);
    }
}