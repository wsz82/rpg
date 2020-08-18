package game.model.plugin;

import java.io.*;

public class LastPluginCaretaker {
    private static final String FILE_NAME = "last_plugin";

    private final File programDir;

    public LastPluginCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void saveMemento(String pluginName) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(pluginName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadMemento() {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME).exists();

        if (!mementoExists) {
            return null;
        }
        String lastPluginDir = null;
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME))
        ) {
            lastPluginDir = (String) os.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lastPluginDir;
    }
}
