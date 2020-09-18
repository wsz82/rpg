package game.model.setting;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SettingMemento implements Externalizable {
    private static final long serialVersionUID = 1L;

    private Settings settings;
    private boolean resizeWithResolution;

    public SettingMemento() {}

    public SettingMemento(Settings settings, boolean resizeWithResolution) {
        this.settings = settings;
        this.resizeWithResolution = resizeWithResolution;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isResizeWithResolution() {
        return resizeWithResolution;
    }

    public void setResizeWithResolution(boolean resizeWithResolution) {
        this.resizeWithResolution = resizeWithResolution;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(settings);
        out.writeBoolean(resizeWithResolution);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        settings = (Settings) in.readObject();
        resizeWithResolution = in.readBoolean();
    }
}
