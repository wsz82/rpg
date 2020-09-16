package io.wsz.model.script;

public enum EqualsOperator {
    EQUAL ("=="),
    NOT_EQUAL ("!=");

    private final String sign;

    EqualsOperator(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }
}
