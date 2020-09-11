package game.model.setting;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SettingMemento implements Externalizable {
    private static final long serialVersionUID = 1L;

    private boolean isFullScreen;
    private Settings settings;
    private boolean resizeWithResolution;

    public SettingMemento() {}

    public SettingMemento(boolean isFullScreen, Settings settings, boolean resizeWithResolution) {
        this.isFullScreen = isFullScreen;
        this.settings = settings;
        this.resizeWithResolution = resizeWithResolution;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
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
        out.writeLong(Sizes.VERSION);

        out.writeBoolean(isFullScreen);
        out.writeObject(settings);
        out.writeBoolean(resizeWithResolution);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        isFullScreen = in.readBoolean();
        settings = (Settings) in.readObject();
        resizeWithResolution = in.readBoolean();
    }
}
