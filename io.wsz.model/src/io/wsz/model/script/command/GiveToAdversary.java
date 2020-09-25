package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.dialog.DialogItem;
import io.wsz.model.dialog.SpeakerMark;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.locale.LocaleKeys;
import io.wsz.model.script.ScriptValidator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

import static io.wsz.model.script.ScriptKeyWords.*;

public class GiveToAdversary implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s, ScriptValidator validator) {
        GiveToAdversary command = new GiveToAdversary();
        s = s.replaceFirst(GIVE_TO_ADVERSARY + REGEX_OPEN_BRACKET + QUOTE, "");
        int nextIndex = s.indexOf(QUOTE);

        if (nextIndex != -1) {
            String itemOrAssetId = s.substring(0, nextIndex);
            command.itemOrAssetId = itemOrAssetId;
            validator.validateItemOrAsset(itemOrAssetId);
            String toRemove = itemOrAssetId + QUOTE + COMMA;
            s = s.replaceFirst(toRemove, "");

            nextIndex = s.indexOf(CLOSE_BRACKET);
            if (nextIndex != -1) {
                String amount = s.substring(0, nextIndex);
                command.amount = amount;
                validator.validateInteger(amount);
                s = s.replaceFirst(amount + REGEX_CLOSE_BRACKET, "");
                validator.validateIsEmpty(s);
                return command;
            }
        }
        validator.setSyntaxInvalid(s);
        return null;
    }

    private String itemOrAssetId;
    private String amount;

    @Override
    public void execute(Controller controller, PosItem giving, PosItem receiving) { //TODO dropping item when does not fits inventory (Problem: items with collision)
        if (giving == null || receiving == null) return;
        boolean receivingOrGivingIsNotContainable = !(receiving instanceof Containable) || !(giving instanceof Containable);
        if (receivingOrGivingIsNotContainable) return;
        Containable givingCo = (Containable) giving;
        Containable receivingCo = (Containable) receiving;

        int amount;
        try {
            amount = Integer.parseInt(this.amount);
        } catch (NumberFormatException e) {
            System.out.println(this.amount + " must be int");
            e.printStackTrace();
            return;
        }
        if (amount < 0) {
            amount = -amount;
            Containable temp = givingCo;
            givingCo = receivingCo;
            receivingCo = temp;
        }

        Equipment equipment = givingCo.getItems().stream()
                .filter(e -> {
                    String id = e.getItemId();
                    if (id != null) {
                        boolean equals = id.equals(itemOrAssetId);
                        if (equals) {
                            return true;
                        } else {
                            return areIdsEqual(e);
                        }
                    } else {
                        return areIdsEqual(e);
                    }
                })
                .findFirst().orElse(null);
        if (equipment == null) return;
        equipment.getPos().reset();

        int availableAmount = 0;
        for (int i = 0; i < amount; i++) {
            if (givingCo.getItems().remove(equipment)) {
                availableAmount++;
            }
        }
        for (int i = 0; i < availableAmount; i++) {
            receivingCo.getItems().add(equipment);
        }

        Creature pc = controller.getDialogMemento().getPc();
        String message;
        Properties locale = controller.getLocale();
        if (givingCo == pc) {
            message = locale.getProperty(LocaleKeys.RETURNED);
        } else {
            message = locale.getProperty(LocaleKeys.RECEIVED);
        }
        message = message + " " + availableAmount + " " + equipment.getName();
        DialogItem di = new DialogItem(SpeakerMark.INFO, "", message);
        controller.getDialogMemento().getDialogs().add(di);
    }

    public boolean areIdsEqual(Equipment e) {
        String id;
        id = e.getAssetId();
        return id.equals(itemOrAssetId);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(itemOrAssetId);
        out.writeObject(amount);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        itemOrAssetId = (String) in.readObject();
        amount = (String) in.readObject();
    }
}
