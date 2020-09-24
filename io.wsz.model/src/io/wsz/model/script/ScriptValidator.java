package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
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

    public ScriptValidator(Controller controller) {
        this.controller = controller;
    }

    public void validateScript(String id) {
        Executable scriptToRun = controller.getScriptById(id);
        if (scriptToRun == null) {
            setScriptIdInvalid(true, id);
        }
    }

    public void validateGlobalVariable(String globalVarID) {
        Variable<?> global = controller.getGlobalVariableById(globalVarID);
        if (global == null) {
            setGlobalVariableInvalid(true, globalVarID);
        }
    }

    public void validateAsset(String assetId) {
        Asset asset = controller.getAssetById(assetId);
        if (asset == null) {
            setAssetIdInvalid(true, assetId);
        }
    }

    public void validateLocation(String id) {
        Location location = controller.getLocationById(id);
        if (location == null) {
            setLocationIdInvalid(true, id);
        }
    }

    public void buildMessage() {
        boolean isValid = !isInvalid;
        if (isValid) {
            message = "";
        } else {
            message = "INVALID";
            if (isSyntaxInvalid) {
                message += "\n SYNTAX: " + code;
            }
            if (isScriptIdInvalid) {
                message += "\n SCRIPT ID: " + scriptId;
            }
            if (isGlobalVariableIdInvalid) {
                message += "\n GLOBAL VARIABLE ID: " + globalVariableId;
            }
            if (isAssetIdInvalid) {
                message += "\n ASSET ID: " + assetId;
            }
            if (isLocationIdInvalid) {
                message += "\n LOCATION ID: " + locationId;
            }
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

    public void setSyntaxInvalid(boolean syntaxInvalid, String code) {
        if (syntaxInvalid) {
            isInvalid = true;
        }
        this.code = code;
        isSyntaxInvalid = syntaxInvalid;
    }

    public void setScriptIdInvalid(boolean scriptIdInvalid, String id) {
        if (scriptIdInvalid) {
            isInvalid = true;
        }
        this.scriptId = id;
        isScriptIdInvalid = scriptIdInvalid;
    }

    public void setGlobalVariableInvalid(boolean globalVariableInvalid, String id) {
        if (globalVariableInvalid) {
            isInvalid = true;
        }
        this.globalVariableId = id;
        isGlobalVariableIdInvalid = globalVariableInvalid;
    }

    public void setAssetIdInvalid(boolean assetIdInvalid, String id) {
        if (assetIdInvalid) {
            isInvalid = true;
        }
        this.assetId = id;
        isAssetIdInvalid = assetIdInvalid;
    }

    public void setLocationIdInvalid(boolean locationIdInvalid, String id) {
        if (locationIdInvalid) {
            isInvalid = true;
        }
        this.locationId = id;
        isLocationIdInvalid = locationIdInvalid;
    }
}
