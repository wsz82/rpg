package game.model.plugin;

import model.plugin.Plugin;
import model.plugin.PluginSerializable;
import model.plugin.SerializableConverter;

import java.io.*;

public class LastPluginCaretaker {
    private static final String FILE_NAME = "last_plugin";

    public void saveMemento(File programDir, Plugin plugin) {
        PluginSerializable ps = SerializableConverter.toPluginSerializable(plugin);
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(ps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin loadMemento(File programDir) {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return new Plugin();
        }
        Plugin memento = new Plugin();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            PluginSerializable ps = (PluginSerializable) os.readObject();

            memento = SerializableConverter.toPlugin(ps);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }
}
