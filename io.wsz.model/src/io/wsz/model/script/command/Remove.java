package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static io.wsz.model.script.ScriptKeyWords.REMOVE;

public class Remove implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s) {
        Remove command = new Remove();
        String closeBracket = ")";
        s = s.replaceFirst(REMOVE + "\\(", "");
        int nextIndex = s.indexOf(closeBracket);

        if (nextIndex != -1) {
            String assetId = s.substring(1, nextIndex - 1);
            command.itemId = assetId;
            return command;
        }
        return null;
    }

    private String itemId;
    private PosItem itemToRemove;
    private Equipment equipmentToRemove;
    private Location locationWithItem;
    private Containable containableWithEquipment;

    @Override
    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        locationWithItem = controller.getCurrentLocation().getLocation();
        List<PosItem> items = locationWithItem.getItems();

        for (PosItem i : items) {
            findItemToRemove(i, locationWithItem);
        }
        tryRemoveItem();
        for (Location l : controller.getLocations()) {
            items = l.getItems();
            for (PosItem i : items) {
                findItemToRemove(i, l);
            }
        }
        tryRemoveItem();
    }

    private void tryRemoveItem() {
        if (itemToRemove != null && locationWithItem != null) {
            locationWithItem.getItemsToRemove().add(itemToRemove);
        } else if (equipmentToRemove != null && containableWithEquipment != null) {
            containableWithEquipment.getItems().remove(equipmentToRemove);
        }
    }

    private void findItemToRemove(PosItem i, Location l) {
        String id = i.getItemId();
        if (id != null) {
            boolean equals = id.equals(itemId);
            if (equals) {
                itemToRemove = i;
                locationWithItem = l;
            }
        }
        if (i instanceof Containable) {
            Containable c = (Containable) i;
            findItemInContainable(c);
        }
    }

    private void findItemInContainable(Containable c) {
        List<Equipment> cItems = c.getItems();
        for (Equipment ci : cItems) {
            String id = ci.getItemId();
            if (id != null) {
                boolean equals = id.equals(itemId);
                if (equals) {
                    equipmentToRemove = ci;
                    containableWithEquipment = c;
                }
            }
            if (ci instanceof Containable) {
                Containable innerC = (Containable) ci;
                findItemInContainable(innerC);
            }
        }
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(itemId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        itemId = (String) in.readObject();
    }
}