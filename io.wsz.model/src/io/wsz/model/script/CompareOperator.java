package io.wsz.model.script;

public enum CompareOperator {
    EQUAL ("=="),
    NOT_EQUAL ("!="),
    GREATER_OR_EQUAL (">="),
    LESSER_OR_EQUAL ("<="),
    GREATER (">"),
    LESSER ("<");

    private final String sign;

    CompareOperator(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public CompareOperator getNegate() {
        return switch (this) {
            case EQUAL -> NOT_EQUAL;
            case NOT_EQUAL -> EQUAL;
            case GREATER -> LESSER_OR_EQUAL;
            case GREATER_OR_EQUAL -> LESSER;
            case LESSER -> GREATER_OR_EQUAL;
            case LESSER_OR_EQUAL -> GREATER;
        };
    }
}
