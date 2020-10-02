package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.script.ScriptValidator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.script.ScriptKeyWords.*;

public class Remove implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s, ScriptValidator validator) {
        Remove command = new Remove();
        s = s.replaceFirst(REMOVE + REGEX_BRACKET_OPEN + QUOTE, "");
        int nextIndex = s.indexOf(QUOTE);

        if (nextIndex != -1) {
            String itemOrAssetId = s.substring(0, nextIndex);
            command.itemOrAssetId = itemOrAssetId;
            validator.validateItemOrAsset(itemOrAssetId);
            s = s.replaceFirst(itemOrAssetId + QUOTE + REGEX_BRACKET_CLOSE, "");
            validator.validateIsEmpty(s);
            return command;
        }
        validator.setSyntaxInvalid(s);
        return null;
    }

    private String itemOrAssetId;

    @Override
    public void execute(Controller controller, PosItem<?, ?> firstAdversary, PosItem<?, ?> secondAdversary) {
        boolean willBeRemoved = controller.getCurrentLocation().tryRemoveItem(itemOrAssetId);
        if (willBeRemoved) return;
        for (Location l : controller.getLocations()) {
            if (l.tryRemoveItem(itemOrAssetId)) break;
        }
    }

    public String getItemOrAssetId() {
        return itemOrAssetId;
    }

    public void setItemOrAssetId(String itemOrAssetId) {
        this.itemOrAssetId = itemOrAssetId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(itemOrAssetId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        itemOrAssetId = (String) in.readObject();
    }
}