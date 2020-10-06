package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.script.ScriptValidator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.script.ScriptKeyWords.*;

public class AddNew implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s, ScriptValidator validator) {
        AddNew command = new AddNew();
        s = s.replaceFirst(ADD_NEW + REGEX_BRACKET_OPEN, "");
        int nextIndex = s.indexOf(COMMA);

        if (nextIndex != -1) {
            String assetId = s.substring(1, nextIndex - 1);
            command.assetId = assetId;
            validator.validateAsset(assetId);
            String toRemove = QUOTE + assetId + QUOTE + COMMA;
            s = s.replaceFirst(toRemove, "");

            nextIndex = s.indexOf(COMMA);
            if (nextIndex != -1) {
                String locationId = s.substring(1, nextIndex - 1);
                command.locationId = locationId;
                validator.validateLocation(locationId);
                toRemove = QUOTE + locationId + QUOTE + COMMA;
                s = s.replaceFirst(toRemove, "");

                nextIndex = s.indexOf(COMMA);
                if (nextIndex != -1) {
                    String level = s.substring(0, nextIndex);
                    command.level = level;
                    validator.validateInteger(level);
                    toRemove = level + COMMA;
                    s = s.replaceFirst(toRemove, "");

                    nextIndex = s.indexOf(COMMA);
                    if (nextIndex != -1) {
                        String posX = s.substring(0, nextIndex);
                        command.posX = posX;
                        validator.validateDecimal(posX);
                        toRemove = posX + COMMA;
                        s = s.replaceFirst(toRemove, "");

                        nextIndex = s.indexOf(COMMA);
                        if (nextIndex == -1) {
                            nextIndex = s.indexOf(BRACKET_CLOSE);
                        }

                        if (nextIndex != -1) {
                            String posY = s.substring(0, nextIndex);
                            command.posY = posY;
                            validator.validateDecimal(command.posY);
                            toRemove = posY;
                            s = s.replaceFirst(toRemove, "");

                            if (s.startsWith(COMMA)) {
                                s = s.replaceFirst(COMMA + QUOTE, "");
                                nextIndex = s.indexOf(QUOTE);
                                if (nextIndex != -1) {
                                    String newItemId = s.substring(0, nextIndex);
                                    command.newItemId = newItemId;
                                    validator.validateNewItemId(newItemId);
                                    s = s.replaceFirst(newItemId + QUOTE + REGEX_BRACKET_CLOSE, "");
                                    validator.validateIsEmpty(s);
                                } else {
                                    parseWithoutNewItemId(s, validator, command);
                                }
                            } else {
                                parseWithoutNewItemId(s, validator, command);
                            }
                            return command;
                        }
                    }
                }
            }
        }
        validator.setSyntaxInvalid(s);
        return null;
    }

    public static void parseWithoutNewItemId(String s, ScriptValidator validator, AddNew command) {
        command.newItemId = null;
        s = s.replaceFirst(REGEX_BRACKET_CLOSE, "");
        validator.validateIsEmpty(s);
    }

    private String assetId;
    private Asset<?> prototype;
    private String locationId;
    private Location location;
    private String level;
    private String posX;
    private String posY;
    private String newItemId;

    @Override
    public void execute(Controller controller, PosItem<?, ?> firstAdversary, PosItem<?, ?> secondAdversary) {
        if (prototype == null) {
            prototype = controller.getAssetById(assetId);
        }
        if (prototype == null) return;
        if (location == null) {
            location = controller.getLocations().stream()
                    .filter(l -> l.getId().equals(locationId))
                    .findFirst().orElse(null);
        }
        if (location == null) return;
        int toLevel = Integer.parseInt(level);
        double toX = Double.parseDouble(posX);
        double toY = Double.parseDouble(posY);
        prototype.addNewItemToLocation(location, toLevel, toX, toY, newItemId);
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
