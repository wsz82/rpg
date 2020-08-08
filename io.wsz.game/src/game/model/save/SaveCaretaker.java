package game.model.save;

import io.wsz.model.Controller;
import io.wsz.model.location.Location;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveCaretaker {
    private static final String FILE_NAME = "saves";

    private final File programDir;

    public SaveCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void createSave(SaveMemento memento) {
        List<Location> locations = new ArrayList<>(Controller.get().getLocationsList());
        memento.setLocations(locations);
        String path = programDir + File.separator + FILE_NAME + File.separator + memento.getName();
        new File(programDir + File.separator + FILE_NAME).mkdir();
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(path))
        ) {
            os.writeObject(memento);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SaveMemento loadMemento(String saveName) {
        boolean mementoExists = new File(programDir + File.separator + FILE_NAME + File.separator + saveName)
                .exists();

        if (!mementoExists) {
            return null;
        }
        SaveMemento memento = new SaveMemento();
        try (
                ObjectInputStream os = new ObjectInputStream(
                        new FileInputStream(programDir + File.separator + FILE_NAME + File.separator + saveName))
        ) {
            memento = (SaveMemento) os.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return memento;
    }

    public void deleteSave(String name) {
        String path = programDir + File.separator + FILE_NAME + File.separator + name;
        File save = new File(path);
        save.delete();
    }

    public List<String> getSavesNames() {
        File savesDir = new File(programDir + File.separator + FILE_NAME);
        boolean savesDirExists = savesDir.exists();

        if (!savesDirExists) {
            return new ArrayList<>(0);
        }
        File[] saves = savesDir.listFiles();
        List<String> names = new ArrayList<>(0);
        for (File save : Objects.requireNonNull(saves)) {
            String name = save.getName();
            names.add(name);
        }
        return names;
    }
}
