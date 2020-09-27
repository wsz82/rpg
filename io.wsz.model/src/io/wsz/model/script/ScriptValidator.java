package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.Creature;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.script.command.Executable;
import io.wsz.model.script.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ScriptValidator {
    private final Controller controller;

    private boolean isInvalid;
    private String message;
    private final List<String> syntaxInvalid = new ArrayList<>(0);
    private final List<String> scriptId = new ArrayList<>(0);
    private final List<String> globalVariableId = new ArrayList<>(0);
    private final List<String> assetId = new ArrayList<>(0);
    private final List<String> locationId = new ArrayList<>(0);
    private final List<String> shouldBeInteger = new ArrayList<>(0);
    private final List<String> shouldBeDecimal = new ArrayList<>(0);
    private final List<String> shouldBeBoolean = new ArrayList<>(0);
    private final List<String> shouldBeCode = new ArrayList<>(0);
    private final List<String> itemId = new ArrayList<>(0);
    private final List<String> itemOrAssetId = new ArrayList<>(0);
    private final List<String> newItemId = new ArrayList<>(0);
    private final List<String> globalBooleanVariableId = new ArrayList<>(0);
    private final List<String> equalsOperator = new ArrayList<>(0);
    private final List<String> creatureId = new ArrayList<>(0);
    private final List<String> inventoryPlaces = new ArrayList<>(0);

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

    public void validateGlobalVariableHasBooleanValue(String id) {
        Variable<?> global = controller.getGlobalVariableById(id);
        if (global == null) {
            setGlobalVariableInvalid(id);
        } else {
            Object variableValue = global.getValue();
            if (!(variableValue instanceof Boolean)) {
                setGlobalBooleanVariableInvalid(id);
            }
        }
    }

    private void setGlobalBooleanVariableInvalid(String id) {
        globalBooleanVariableId.add(id);
    }

    private void validateBoolean(String value) {
        boolean isNotTrueOrFalse = !value.equals("true") && !value.equals("false");
        if (isNotTrueOrFalse) {
            isInvalid = true;
            shouldBeBoolean.add(value);
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
        PosItem item = controller.getItemOrAssetById(id);
        if (item == null) {
            setItemOrAssetIdInvalid(id);
        }
    }

    public void validateCreature(String id) {
        PosItem item = controller.getItemOrAssetById(id);
        if (!(item instanceof Creature)) {
            setCreatureIdInvalid(id);
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
            shouldBeInteger.add(integer);
        }
    }

    public void validateDecimal(String decimal) {
        try {
            Double.parseDouble(decimal);
        } catch (NumberFormatException e) {
            isInvalid = true;
            shouldBeDecimal.add(decimal);
        }
    }

    public void validateIsEmpty(String s) {
        boolean isNotRecognized = !s.isEmpty();
        if (isNotRecognized) {
            isInvalid = true;
            shouldBeCode.add(s);
        }
    }

    public void validateShouldNotBeEmpty(String firstComponent) {
        if (firstComponent.isEmpty()) {
            isInvalid = true;
            syntaxInvalid.add("is empty");
        }
    }

    public void validateEqualsOperator(EqualsOperator equalsOperator, String operator) {
        if (equalsOperator == null) {
            setEqualsOperatorInvalid(operator);
        }
    }

    public void validateInventoryPlaceId(String id) {
        InventoryPlaceType placeType = controller.getInventoryPlaceById(id);
        if (placeType == null) {
            setInventoryPlaceInvalid(id);
        }
    }

    public void buildMessage() {
        StringBuilder builder = new StringBuilder();
        if (isInvalid) {
            builder.append("INVALID");
            if (!syntaxInvalid.isEmpty()) {
                builder.append("\n SYNTAX");
                iterateToBuildMessage(builder, syntaxInvalid);
            }
            if (!scriptId.isEmpty()) {
                builder.append("\n SCRIPT ID");
                iterateToBuildMessage(builder, scriptId);
            }
            if (!globalVariableId.isEmpty()) {
                builder.append("\n GLOBAL VARIABLE ID");
                iterateToBuildMessage(builder, globalVariableId);
            }

            if (!globalBooleanVariableId.isEmpty()) {
                builder.append("\n GLOBAL BOOlEAN VARIABLE ID");
                iterateToBuildMessage(builder, globalBooleanVariableId);
            }
            if (!assetId.isEmpty()) {
                builder.append("\n ASSET ID");
                iterateToBuildMessage(builder, assetId);
            }
            if (!itemId.isEmpty()) {
                builder.append("\n ITEM ID");
                iterateToBuildMessage(builder, itemId);
            }
            if (!itemOrAssetId.isEmpty()) {
                builder.append("\n ITEM OR ASSET ID");
                iterateToBuildMessage(builder, itemOrAssetId);
            }
            if (!creatureId.isEmpty()) {
                builder.append("\n CREATURE ID");
                iterateToBuildMessage(builder, creatureId);
            }
            if (!newItemId.isEmpty()) {
                builder.append("\n NEW ITEM ID ALREADY EXISTS");
                iterateToBuildMessage(builder, newItemId);
            }
            if (!locationId.isEmpty()) {
                builder.append("\n LOCATION ID");
                iterateToBuildMessage(builder, locationId);
            }
            if (!inventoryPlaces.isEmpty()) {
                builder.append("\n INVENTORY PLACE ID");
                iterateToBuildMessage(builder, inventoryPlaces);
            }
            if (!shouldBeInteger.isEmpty()) {
                builder.append("\n INTEGER");
                iterateToBuildMessage(builder, shouldBeInteger);
            }
            if (!shouldBeDecimal.isEmpty()) {
                builder.append("\n DECIMAL");
                iterateToBuildMessage(builder, shouldBeDecimal);
            }
            if (!shouldBeBoolean.isEmpty()) {
                builder.append("\n BOOLEAN");
                iterateToBuildMessage(builder, shouldBeBoolean);
            }
            if (!equalsOperator.isEmpty()) {
                builder.append("\n BOOLEAN");
                iterateToBuildMessage(builder, equalsOperator);
            }
            if (!shouldBeCode.isEmpty()) {
                builder.append("\n UNRECOGNIZED");
                iterateToBuildMessage(builder, shouldBeCode);
            }
        }
        message = builder.toString();
    }

    public void iterateToBuildMessage(StringBuilder builder, List<String> invalids) {
        for (String message : invalids) {
            builder.append(" | ").append(message);
        }
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
        this.syntaxInvalid.add(code);
    }

    public void setScriptIdInvalid(String id) {
        isInvalid = true;
        this.scriptId.add(id);
    }

    public void setGlobalVariableInvalid(String id) {
        isInvalid = true;
        this.globalVariableId.add(id);
    }

    public void setAssetIdInvalid(String id) {
        isInvalid = true;
        this.assetId.add(id);
    }

    private void setItemIdInvalid(String id) {
        isInvalid = true;
        this.itemId.add(id);
    }

    private void setItemOrAssetIdInvalid(String id) {
        isInvalid = true;
        this.itemOrAssetId.add(id);
    }

    private void setCreatureIdInvalid(String id) {
        isInvalid = true;
        this.creatureId.add(id);
    }

    private void setNewItemIdInvalid(String id) {
        isInvalid = true;
        this.newItemId.add(id);
    }

    public void setLocationIdInvalid(String id) {
        isInvalid = true;
        this.locationId.add(id);
    }

    private void setEqualsOperatorInvalid(String operator) {
        isInvalid = true;
        this.equalsOperator.add(operator);
    }

    private void setInventoryPlaceInvalid(String id) {
        isInvalid = true;
        this.inventoryPlaces.add(id);
    }
}
