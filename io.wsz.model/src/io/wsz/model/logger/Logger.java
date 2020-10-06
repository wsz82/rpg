package io.wsz.model.logger;

public class Logger {
    private long viewStart;
    private long modelStart;

    public void logTimeBetweenModelStarts(long startNext) {
        if (modelStart != 0) {
            long dif = startNext - modelStart;
            if (dif > 20) {
                System.out.println("Model: millis between loop starts: " + dif);
            }
        }
        modelStart = startNext;
    }

    public void logTimeOfModelLoopDuration(long dif) {
        if (dif > 20) {
            System.out.println("Model: millis loop duration: " + dif);
        }
    }

    public void logTimeBetweenViewStarts() {
        long startNext = System.currentTimeMillis();
        if (viewStart != 0) {
            long dif = startNext - viewStart;
            if (dif > 30) {
                System.out.println("View: millis between loop starts: " + dif);
            }
        }
        viewStart = startNext;
    }

    public void logTimeOfViewLoopDuration() {
        long end = System.currentTimeMillis();
        if (viewStart != 0) {
            long dif = end - viewStart;
            if (dif > 20) {
                System.out.println("View: millis loop duration: " + dif);
            }
        }
    }

    public void logNoAnsweringResponse(String name) {
        System.out.println(name + " does not respond");
    }

    public void logAssetReloadImagesError(String assetId) {
        System.out.println("Error with " + assetId);
    }

    public void logItemRemoved(String item, String from) {
        System.out.println(item + " removed from " + from);
    }

    public void logItemDoesNotFit(String item, String what) {
        System.out.println(item + " does not fit " + what);
    }

    public void logItemAddedTo(String item, String what) {
        System.out.println(item + " added to " + what);
    }

    public void logItemUnequipped(String item, String place) {
        System.out.println(item + " unequipped from " + place);
    }

    public void logItemCouldntBeUnequipped(String item, String place) {
        System.out.println(item + " could not be unequipped from " + place);
    }

    public void logItemEquipped(String item, String place) {
        System.out.println(item + " equipped on place " + place);
    }

    public void logItemRemovedFromInventory(String item, String creatureName) {
        System.out.println(item + " removed from " + creatureName + " inventory");
    }

    public void logItemDoesNotFitInventory(String item, String creatureName) {
        System.out.println(item + " does not fit " + creatureName + " inventory");
    }

    public void logItemAddedToInventory(String item, String creatureName) {
        System.out.println(item + " added to " + creatureName + " inventory");
    }

    public void logItemMovedToInventory(String item, String creatureName) {
        System.out.println(item + " moved to " + creatureName + " inventory");
    }

    public void logOneCreatureOutOfAnotherCreatureRange(String one, String another) {
        System.out.println(one + " out of " + another + " range");
    }

    public void logContainerSearchedBy(String container, String creature) {
        System.out.println(container + " searched by " + creature);
    }

    public void logItemCannotBeActionBecauseCollides(String item, String message, String another) {
        System.out.println(item + " cannot be " + message + ": collides with " + another);
    }

    public void logItemAction(String item, String message) {
        System.out.println(item + " " + message);
    }

    public void logItemCannotBeTakenBecauseIsBehind(String item, String obstacle) {
        System.out.println(item + " cannot be taken: behind " + obstacle);
    }

    public void logItemCannotBeActionBecauseIsBlockedBehind(String item, String message, String another) {
        System.out.println(item + " cannot be " +  message + ": " + another + " blocks behind");
    }

    public void logItemCollides(String one, String another) {
        System.out.println(one + " collides " + another);
    }

    public void logWayCollision() {
        System.out.println("Way collision");
    }

    public void logCannotGive(String name) {
        System.out.println(name + " is not containable");
    }

    public void logCannotReceive(String name) {
        logCannotGive(name);
    }
}
