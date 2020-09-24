package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.*;
import io.wsz.model.location.Location;
import io.wsz.model.script.ScriptValidator;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.script.ScriptKeyWords.ADD_NEW;

public class AddNew implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s, ScriptValidator validator) {
        AddNew command = new AddNew();
        String quote = "\"";
        String comma = ",";
        s = s.replaceFirst(ADD_NEW + "\\(", "");
        int nextIndex = s.indexOf(comma);

        if (nextIndex != -1) {
            String assetId = s.substring(1, nextIndex - 1);
            command.assetId = assetId;
            validator.validateAsset(assetId);
            String toRemove = quote + assetId + quote + comma;
            s = s.replace(toRemove, "");

            nextIndex = s.indexOf(comma);
            if (nextIndex != -1) {
                String locationId = s.substring(1, nextIndex - 1);
                command.locationId = locationId;
                validator.validateLocation(locationId);
                toRemove = quote + locationId + quote + comma;
                s = s.replace(toRemove, "");

                nextIndex = s.indexOf(comma);
                if (nextIndex != -1) {
                    String level = s.substring(0, nextIndex);
                    command.level = level;
                    toRemove = level + comma;
                    s = s.replace(toRemove, "");

                    nextIndex = s.indexOf(comma);
                    if (nextIndex != -1) {
                        String posX = s.substring(0, nextIndex);
                        command.posX = posX;
                        toRemove = posX + comma;
                        s = s.replace(toRemove, "");

                        int closeIndex = s.indexOf(")");
                        if (closeIndex != -1) {
                            command.posY = s.substring(0, closeIndex);
                            return command;
                        }
                    }
                }
            }
        }
        validator.setSyntaxInvalid(true, s);
        return null;
    }

    private String assetId;
    private String locationId;
    private String level;
    private String posX;
    private String posY;

    @Override
    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        Asset prototype = controller.getAssets().stream()
                .filter(a -> a.getAssetId().equals(assetId))
                .findFirst().orElse(null);
        if (prototype == null) return;
        Location toLocation = controller.getLocations().stream()
                .filter(l -> l.getId().equals(locationId))
                .findFirst().orElse(null);
        int toLevel = Integer.parseInt(level);
        double toX = Double.parseDouble(posX);
        double toY = Double.parseDouble(posY);

        if (prototype instanceof Container) {
            Container container = new Container((Container) prototype);
            setUpItem(container, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Cover) {
            Cover cover = new Cover((Cover) prototype);
            setUpItem(cover, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Creature) {
            Creature creature = new Creature((Creature) prototype);
            setUpCreatureItem(creature, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof InDoor) {
            InDoor inDoor = new InDoor((InDoor) prototype);
            setUpItem(inDoor, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Landscape) {
            Landscape landscape = new Landscape((Landscape) prototype);
            setUpItem(landscape, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Misc){
            Misc misc = new Misc((Misc) prototype);
            setUpItem(misc, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof OutDoor) {
            OutDoor outDoor = new OutDoor((OutDoor) prototype);
            setUpItem(outDoor, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Teleport) {
            Teleport teleport = new Teleport((Teleport) prototype);
            setUpItem(teleport, toLocation, toLevel, toX, toY);
        } else if (prototype instanceof Weapon) {
            Weapon weapon = new Weapon((Weapon) prototype);
            setUpItem(weapon, toLocation, toLevel, toX, toY);
        }
    }

    private void setUpCreatureItem(Creature creature, Location toLocation, int toLevel, double toX, double toY) {
        Coords centered = creature.getReversedCenter(toX, toY, toLevel, toLocation);
        creature.setPos(centered);
        toLocation.getItemsToAdd().add(creature);
    }

    private void setUpItem(PosItem item, Location toLocation, int toLevel, double toX, double toY) {
        item.setPos(toX, toY, toLevel, toLocation);
        toLocation.getItemsToAdd().add(item);
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(assetId);
        out.writeObject(locationId);
        out.writeObject(level);
        out.writeObject(posX);
        out.writeObject(posY);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        assetId = (String) in.readObject();
        locationId = (String) in.readObject();
        level = (String) in.readObject();
        posX = (String) in.readObject();
        posY = (String) in.readObject();
    }
}
