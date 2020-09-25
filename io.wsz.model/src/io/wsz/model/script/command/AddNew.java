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
                    validator.validateInteger(level);
                    toRemove = level + comma;
                    s = s.replace(toRemove, "");

                    nextIndex = s.indexOf(comma);
                    if (nextIndex != -1) {
                        String posX = s.substring(0, nextIndex);
                        command.posX = posX;
                        validator.validateDecimal(posX);
                        toRemove = posX + comma;
                        s = s.replace(toRemove, "");

                        int closeIndex = s.indexOf(")");
                        if (closeIndex != -1) {
                            command.posY = s.substring(0, closeIndex);
                            validator.validateDecimal(command.posY);
                            return command;
                        }
                    }
                }
            }
        }
        validator.setSyntaxInvalid(s);
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
        prototype.addNewItemToLocation(toLocation, toLevel, toX, toY);
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
