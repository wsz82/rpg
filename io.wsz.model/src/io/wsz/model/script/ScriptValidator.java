package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.script.command.Executable;
import io.wsz.model.script.variable.Variable;

public class ScriptValidator {
    private final Controller controller;

    private boolean isInvalid;
    private String message;
    private boolean isSyntaxInvalid;
    private String code;
    private boolean isScriptIdInvalid;
    private String scriptId;
    private boolean isGlobalVariableIdInvalid;
    private String globalVariableId;
    private boolean isAssetIdInvalid;
    private String assetId;
    private boolean isLocationIdInvalid;
    private String locationId;
    private boolean isIntegerInvalid;
    private String shouldBeInteger;
    private boolean isDecimalInvalid;
    private String shouldBeDecimal;
    private boolean isBooleanInvalid;
    private String shouldBeBoolean;
    private boolean isUnrecognized;
    private String shouldBeCode;
    private boolean isItemIdInvalid;
    private String itemId;
    private boolean isItemOrAssetIdInvalid;
    private String itemOrAssetId;
    private boolean isNewItemIdInvalid;
    private String newItemId;

    public ScriptValidator(Controller controller) {
        this.controller = controller;
    }

    public void validateScript(String id) {
        Executable scriptToRun = controller.getScriptById(id);
        if (scriptToRun == null) {
            setScriptIdInvalid(id);
        }
    }

    public void validateGlobalVariable(String globalVarID, String value) {
        Variable<?> global = controller.getGlobalVariableById(globalVarID);
        if (global == null) {
            setGlobalVariableInvalid(globalVarID);
        } else {
            Object variableValue = global.getValue();
            if (variableValue instanceof Boolean) {
                validateBoolean(value);
            } else if (variableValue instanceof Integer) {
                validateInteger(value);
            } else if (variableValue instanceof Double) {
                validateDecimal(value);
            }
        }
    }

    private void validateBoolean(String value) {
        boolean isNotTrueOrFalse = !value.equals("true") && !value.equals("false");
        if (isNotTrueOrFalse) {
            isInvalid = true;
            isBooleanInvalid = true;
            shouldBeBoolean = value;
        }
    }

    public void validateAsset(String assetId) {
        Asset asset = controller.getAssetById(assetId);
        if (asset == null) {
            setAssetIdInvalid(assetId);
        }
    }

    public void validateItem(String itemID) {
        PosItem item = controller.getItemByItemId(itemID);
        if (item == null) {
            setItemIdInvalid(itemID);
        }
    }

    public void validateItemOrAsset(String id) {
        PosItem item = controller.getItemByItemId(id);
        if (item == null) {
            item = controller.getItemByAssetId(id);
            if (item == null) {
                setItemOrAssetIdInvalid(id);
            }
        }
    }

    public void validateNewItemId(String id) { //TODO read also ids in scripts (include dialogs scripts)
        if (controller.getItemByItemId(id) != null || controller.getItemByAssetId(id) != null) {
            setNewItemIdInvalid(id);
        }
    }

    public void validateLocation(String id) {
        Location location = controller.getLocationById(id);
        if (location == null) {
            setLocationIdInvalid(id);
        }
    }

    public void validateInteger(String integer) {
        try {
            Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            isInvalid = true;
            isIntegerInvalid = true;
            shouldBeInteger = integer;
        }
    }

    public void validateDecimal(String decimal) {
        try {
            Double.parseDouble(decimal);
        } catch (NumberFormatException e) {
            isInvalid = true;
            isDecimalInvalid = true;
            shouldBeDecimal = decimal;
        }
    }

    public void validateIsEmpty(String s) {
        boolean isNotRecognized = !s.isEmpty();
        if (isNotRecognized) {
            isInvalid = true;
            isUnrecognized = true;
            shouldBeCode = s;
        }
    }

    public void buildMessage() {
        StringBuilder builder = new StringBuilder();
        if (isInvalid) {
            builder.append("INVALID");
            if (isSyntaxInvalid) {
                builder.append("\n SYNTAX: ").append(code);
            }
            if (isScriptIdInvalid) {
                builder.append("\n SCRIPT ID: ").append(scriptId);
            }
            if (isGlobalVariableIdInvalid) {
                builder.append("\n GLOBAL VARIABLE ID: ").append(globalVariableId);
            }
            if (isAssetIdInvalid) {
                builder.append("\n ASSET ID: ").append(assetId);
            }
            if (isItemIdInvalid) {
                builder.append("\n ITEM ID: ").append(itemId);
            }
            if (isItemOrAssetIdInvalid) {
                builder.append("\n ITEM OR ASSET ID: ").append(itemOrAssetId);
            }
            if (isNewItemIdInvalid) {
                builder.append("\n NEW ITEM ID ALREADY EXISTS: ").append(newItemId);
            }
            if (isLocationIdInvalid) {
                builder.append("\n LOCATION ID: ").append(locationId);
            }
            if (isIntegerInvalid) {
                builder.append("\n INTEGER: ").append(shouldBeInteger);
            }
            if (isDecimalInvalid) {
                builder.append("\n DECIMAL: ").append(shouldBeDecimal);
            }
            if (isBooleanInvalid) {
                builder.append("\n BOOLEAN: ").append(shouldBeBoolean);
            }
            if (isUnrecognized) {
                builder.append("\n UNRECOGNIZED: ").append(shouldBeCode);
            }
        }
        message = builder.toString();
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSyntaxInvalid(String code) {
        isInvalid = true;
        this.code = code;
        isSyntaxInvalid = true;
    }

    public void setScriptIdInvalid(String id) {
        isInvalid = true;
        this.scriptId = id;
        isScriptIdInvalid = true;
    }

    public void setGlobalVariableInvalid(String id) {
        isInvalid = true;
        this.globalVariableId = id;
        isGlobalVariableIdInvalid = true;
    }

    public void setAssetIdInvalid(String id) {
        isInvalid = true;
        this.assetId = id;
        isAssetIdInvalid = true;
    }

    private void setItemIdInvalid(String id) {
        isInvalid = true;
        this.itemId = id;
        isItemIdInvalid = true;
    }

    private void setItemOrAssetIdInvalid(String id) {
        isInvalid = true;
        this.itemOrAssetId = id;
        isItemOrAssetIdInvalid = true;
    }

    private void setNewItemIdInvalid(String id) {
        isInvalid = true;
        this.newItemId = id;
        isNewItemIdInvalid = true;
    }

    public void setLocationIdInvalid(String id) {
        isInvalid = true;
        this.locationId = id;
        isLocationIdInvalid = true;
    }
}
