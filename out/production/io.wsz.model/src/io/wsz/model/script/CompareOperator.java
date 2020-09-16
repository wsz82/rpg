package io.wsz.model.script;

public enum CompareOperator {
    EQUAL ("=="),
    NOT_EQUAL ("!="),
    GREATER (">"),
    GREATER_OR_EQUAL (">="),
    LESSER ("<"),
    LESSER_OR_EQUAL ("<=");

    private final String sign;

    CompareOperator(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }
}
