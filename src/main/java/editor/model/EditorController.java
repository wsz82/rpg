package editor.model;

public class EditorController {
    private static EditorController singleton;

    public static EditorController get() {
        if (singleton == null) {
            singleton = new EditorController();
        }
        return singleton;
    }

    private EditorController(){}

    public ActiveContent getActiveContent() {
        return ActiveContent.get();
    }
}
