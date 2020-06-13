package io.wsz.model.stage;

public class BoardPos {
    private static Coords boardPos;

    public static Coords getBoardPos() {
        if (boardPos == null) {
            boardPos = new Coords(0, 0);
        }
        return boardPos;
    }

    private BoardPos(){}
}
