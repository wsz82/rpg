package game.model.setting;

import io.wsz.model.sizes.Sizes;

import java.io.*;

public class SettingCaretaker {
    private static final String FILE_NAME = "game_settings";
    private final File programDir;

    public SettingCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void saveMemento(SettingMemento memento) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(memento);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SettingMemento loadMemento() {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return new SettingMemento(true, Sizes.getFontSize(),
                    Settings.getGameScrollSpeed(), Settings.getDialogScrollSpeed(), Settings.isCenterOnPC(),
                    Settings.isPauseOnInventory(), Settings.getResolutionWidth(), Settings.getResolutionHeight(),
                    Sizes.isResizeWithResolution());
        }
        SettingMemento memento = new SettingMemento();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            memento = (SettingMemento) os.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }
}
