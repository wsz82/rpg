package game.model.save;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SaveCaretaker {
    private static final String SAVES_DIR = File.separator + "saves";
    private static final String TEMP_SAVE = "temp";
    private static final String TEMP_SAVE_DIR = File.separator + TEMP_SAVE;

    private final File programDir;

    public SaveCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void makeSave(SaveMemento memento) {
        String savesProgramDir = programDir + SAVES_DIR;
        File savesFile = new File(savesProgramDir);
        if (!savesFile.exists()) {
            savesFile.mkdir();
        }
        File tempFile = new File(savesProgramDir + TEMP_SAVE_DIR);
        try (
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {
            os.writeObject(memento);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String path = savesProgramDir + File.separator + memento.getName();
        File saveFile = new File(path);
        Path temp = Path.of(tempFile.toURI());
        Path realSave = Path.of(saveFile.toURI());
        try {
            Files.copy(temp, realSave, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        tempFile.delete();
    }

    public SaveMemento loadMemento(String saveName) {
        boolean mementoExists = new File(programDir + File.separator + SAVES_DIR + File.separator + saveName)
                .exists();

        if (!mementoExists) {
            return null;
        }
        SaveMemento memento = new SaveMemento();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + SAVES_DIR + File.separator + saveName))
        ) {
            memento = (SaveMemento) os.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }

    public void deleteSave(String name) {
        String path = programDir + File.separator + SAVES_DIR + File.separator + name;
        File save = new File(path);
        save.delete();
    }

    public List<String> getSavesNames() {
        File savesDir = new File(programDir + File.separator + SAVES_DIR);
        List<String> names = new ArrayList<>(0);
        boolean savesDirNotExists = !savesDir.exists();
        if (savesDirNotExists) return names;
        File[] saves = savesDir.listFiles();
        if (saves == null) return names;
        for (File save : saves) {
            String name = save.getName();
            if (name.equals(TEMP_SAVE)) continue;
            names.add(name);
        }
        return names;
    }
}
