package view.stage;

import java.io.*;

class SettingsMemento implements Serializable{
    private static final String FILE_NAME = "settings";

    private double stageX;
    private double stageY;
    private double stageWidth;
    private double stageHeight;

    private double layersX;
    private double layersY;
    private double layersWidth;
    private double layersHeight;

    private double assetsX;
    private double assetsY;
    private double assetsWidth;
    private double assetsHeight;

    private double contentsX;
    private double contentsY;
    private double contentsWidth;
    private double contentsHeight;

    private double locationsX;
    private double locationsY;
    private double locationsWidth;
    private double locationsHeight;

    SettingsMemento(){};

    SettingsMemento(double stageX, double stageY, double stageWidth, double stageHeight,
                    double layersX, double layersY, double layersWidth, double layersHeight,
                    double assetsX, double assetsY, double assetsWidth, double assetsHeight,
                    double contentsX, double contentsY, double contentsWidth, double contentsHeight,
                    double locationsX, double locationsY, double locationsWidth, double locationsHeight) {
        this.stageX = stageX;
        this.stageY = stageY;
        this.stageWidth = stageWidth;
        this.stageHeight = stageHeight;
        this.layersX = layersX;
        this.layersY = layersY;
        this.layersWidth = layersWidth;
        this.layersHeight = layersHeight;
        this.assetsX = assetsX;
        this.assetsY = assetsY;
        this.assetsWidth = assetsWidth;
        this.assetsHeight = assetsHeight;
        this.contentsX = contentsX;
        this.contentsY = contentsY;
        this.contentsWidth = contentsWidth;
        this.contentsHeight = contentsHeight;
        this.locationsX = locationsX;
        this.locationsY = locationsY;
        this.locationsWidth = locationsWidth;
        this.locationsHeight = locationsHeight;
    }

    void saveMemento(File programDir) {
        try (
                ObjectOutputStream os = new ObjectOutputStream(
                        new FileOutputStream(programDir + File.separator + FILE_NAME))
        ) {
            os.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SettingsMemento loadMemento (File programDir) {
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

    public double getStageX() {
        return stageX;
    }

    public double getStageY() {
        return stageY;
    }

    public double getStageWidth() {
        return stageWidth;
    }

    public double getStageHeight() {
        return stageHeight;
    }

    public double getLayersX() {
        return layersX;
    }

    public double getLayersY() {
        return layersY;
    }

    public double getLayersWidth() {
        return layersWidth;
    }

    public double getLayersHeight() {
        return layersHeight;
    }

    public double getAssetsX() {
        return assetsX;
    }

    public double getAssetsY() {
        return assetsY;
    }

    public double getAssetsWidth() {
        return assetsWidth;
    }

    public double getAssetsHeight() {
        return assetsHeight;
    }

    public double getContentsX() {
        return contentsX;
    }

    public double getContentsY() {
        return contentsY;
    }

    public double getContentsWidth() {
        return contentsWidth;
    }

    public double getContentsHeight() {
        return contentsHeight;
    }

    public double getLocationsX() {
        return locationsX;
    }

    public double getLocationsY() {
        return locationsY;
    }

    public double getLocationsWidth() {
        return locationsWidth;
    }

    public double getLocationsHeight() {
        return locationsHeight;
    }
}
