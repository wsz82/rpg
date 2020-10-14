package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.dialog.DialogItem;
import io.wsz.model.dialog.SpeakerMark;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.list.EquipmentList;
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
        s = s.replaceFirst(GIVE_TO_ADVERSARY + REGEX_BRACKET_OPEN + QUOTE, "");
        int nextIndex = s.indexOf(QUOTE);

        if (nextIndex != -1) {
            String itemOrAssetId = s.substring(0, nextIndex);
            command.itemOrAssetId = itemOrAssetId;
            validator.validateItemOrAsset(itemOrAssetId);
            String toRemove = itemOrAssetId + QUOTE + COMMA;
            s = s.replaceFirst(toRemove, "");

            nextIndex = s.indexOf(BRACKET_CLOSE);
            if (nextIndex != -1) {
                String amount = s.substring(0, nextIndex);
                command.amount = amount;
                validator.validateInteger(amount);
                s = s.replaceFirst(amount + REGEX_BRACKET_CLOSE, "");
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
    public boolean tryExecute(Controller controller, PosItem<?, ?> giver, PosItem<?, ?> receiver) { //TODO dropping item when does not fits inventory (Problem: items with collision)
        if (giver == null || receiver == null) return false;
        ItemMover itemMover = (concreteGiver, concreteReceiver) -> {
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
                Containable temp = concreteGiver;
                concreteGiver = concreteReceiver;
                concreteReceiver = temp;
            }

            EquipmentList equipmentList = concreteGiver.getEquipmentList();
            Equipment<?,?> equipment = equipmentList.getItemByItemOrAssetId(itemOrAssetId);
            if (equipment == null) return;
            equipment.getPos().reset();

            int availableAmount = 0;
            for (int i = 0; i < amount; i++) {
                if (equipmentList.contains(equipment)) {
                    equipmentList.remove(equipment);
                    availableAmount++;
                }
            }
            for (int i = 0; i < availableAmount; i++) {
                concreteReceiver.getEquipmentList().add(equipment);
            }

            Creature pc = controller.getDialogMemento().getPc();
            String message;
            Properties locale = controller.getLocale();
            if (concreteGiver == pc) {
                message = locale.getProperty(LocaleKeys.RETURNED);
            } else {
                message = locale.getProperty(LocaleKeys.RECEIVED);
            }
            message = message + " " + availableAmount + " " + equipment.getName();
            DialogItem di = new DialogItem(SpeakerMark.INFO, "", message);
            controller.getDialogMemento().getDialogs().add(di);
        };
        giver.moveItemTo(receiver, itemMover);
        return true;
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
