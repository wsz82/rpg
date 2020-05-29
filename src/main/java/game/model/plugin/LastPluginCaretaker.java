package game.model.plugin;

import java.io.*;

public class LastPluginCaretaker {
    private static final String FILE_NAME = "last_plugin";

    public void saveMemento(File programDir, File pluginDir) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(pluginDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File loadMemento(File programDir) {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return null;
        }
        File lastPluginDir = null;
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            lastPluginDir = (File) os.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lastPluginDir;
    }
}
