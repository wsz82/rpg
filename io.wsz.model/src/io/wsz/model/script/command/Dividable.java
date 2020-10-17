package io.wsz.model.script.command;

public interface Dividable {

    default int getDivisionCloseIndex(String stringWithDivision) {
        return getDivisionCloseIndex(stringWithDivision, stringWithDivision.indexOf(getDivisionClose()));
    }

    private int getDivisionCloseIndex(String stringWithDivision, int nextBlockClose) {
        if (nextBlockClose == -1) {
            return nextBlockClose;
        }
        String codeToNextBlockClose = stringWithDivision.substring(0, nextBlockClose + 1);
        int openings = 0;
        int closings = 0;
        for (int i = 0; i < codeToNextBlockClose.length(); i++) {
            if (codeToNextBlockClose.charAt(i) == getCharDivisionOpen()) {
                openings++;
            } else if (codeToNextBlockClose.charAt(i) == getCharDivisionClose()) {
                closings++;
            }
        }

        if (openings == closings) {
            return nextBlockClose;
        } else {
            nextBlockClose = stringWithDivision.indexOf(getDivisionClose(), nextBlockClose + 1);
            return getDivisionCloseIndex(stringWithDivision, nextBlockClose);
        }
    }

    String getDivisionClose();

    char getCharDivisionClose();

    char getCharDivisionOpen();
}
