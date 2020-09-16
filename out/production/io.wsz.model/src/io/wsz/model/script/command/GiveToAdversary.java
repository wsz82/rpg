package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.dialog.DialogItem;
import io.wsz.model.dialog.SpeakerMark;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.locale.LocaleKeys;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

import static io.wsz.model.script.ScriptKeyWords.GIVE_TO_ADVERSARY;

public class GiveToAdversary implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s) {
        GiveToAdversary command = new GiveToAdversary();
        s = s.replaceFirst(GIVE_TO_ADVERSARY + "\\(\"", "");
        String comma = "\",";
        int commaIndex = s.indexOf(comma);

        if (commaIndex != -1) {
            String itemID = s.substring(0, commaIndex);
            command.itemID = itemID;
            String toRemove = itemID + comma;
            s = s.replace(toRemove, "");

            int closeIndex = s.indexOf(")");
            if (closeIndex != -1) {
                String amount = s.substring(0, closeIndex);
                command.amount = amount;
                return command;
            }
        }
        return null;
    }

    private String itemID;
    private String amount;

    public GiveToAdversary() {
    }

    @Override
    public void execute(Controller controller, PosItem giving, PosItem receiving) {
        if (giving == null || receiving == null) return;

        Equipment equipment;
        if (giving instanceof Containable) {
            Containable givingCo = (Containable) giving;
            equipment = givingCo.getItems().stream()
                    .filter(e -> e.getAssetId().equals(itemID))
                    .findFirst()
                    .orElse(null);
            if (equipment == null) return;
            if (receiving instanceof Containable) { //TODO dropping item when does not fits inventory (Problem: items with collision)
                Containable receivingCo = (Containable) receiving;
                equipment.getPos().reset();

                int amount;
                try {
                    amount = Integer.parseInt(this.amount);
                } catch (NumberFormatException e) {
                    System.out.println(this.amount + " must be int");
                    e.printStackTrace();
                    return;
                }
                if (amount < 1) {
                    System.out.println(amount + " must be positive");
                    return;
                }
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
                if (giving == pc) {
                    message = locale.getProperty(LocaleKeys.RETURNED);
                } else {
                    message = locale.getProperty(LocaleKeys.RECEIVED);
                }
                message = message + " " + availableAmount + " " + equipment.getName();
                DialogItem di = new DialogItem(SpeakerMark.INFO, "", message);
                controller.getDialogMemento().getDialogs().add(di);
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(itemID);
        out.writeObject(amount);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        itemID = (String) in.readObject();
        amount = (String) in.readObject();
    }
}
