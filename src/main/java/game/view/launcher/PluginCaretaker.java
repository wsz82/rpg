package game.view.launcher;

import model.plugin.Plugin;

import java.io.*;

class PluginCaretaker {
    private static final String FILE_NAME = "plugins";

    void saveMemento(File programDir, PluginMemento memento) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(memento);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    PluginMemento loadMemento (File programDir) {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return new PluginMemento();
        }
        PluginMemento memento = new PluginMemento();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            memento = (PluginMemento) os.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }
}
