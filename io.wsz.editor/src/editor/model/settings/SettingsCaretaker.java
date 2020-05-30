package editor.model.settings;

import java.io.*;

public class SettingsCaretaker {
    private static final String FILE_NAME = "editor_settings";
    private final File programDir;

    public SettingsCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void saveMemento(SettingsMemento memento) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(memento);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SettingsMemento loadMemento () {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return new SettingsMemento(
                    0, 0, 800, 600,
                    0, 600, 300, 300,
                    800, 600, 400, 300,
                    300, 600, 500, 300,
                    800, 0, 400,600
            );
        }
        SettingsMemento memento = new SettingsMemento();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            memento = (SettingsMemento) os.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }
}
